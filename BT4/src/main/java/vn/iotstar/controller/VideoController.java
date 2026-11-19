package vn.iotstar.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Video;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.form.VideoForm;
import vn.iotstar.util.Constant;
import vn.iotstar.util.ValidationUtil;

/**
 * Bài 4: CRUD Videos + danh sách video của user (phân trang 6/trang) + chi tiết/play.
 *   GET  /video          : danh sách video của user đang đăng nhập, phân trang
 *   GET  /video/add      : form thêm
 *   POST /video/insert   : thêm (multipart: poster + file video HOẶC link YouTube)
 *   GET  /video/edit     : form sửa
 *   POST /video/update   : sửa
 *   POST /video/delete   : xóa
 *   GET  /video/detail   : chi tiết + play (ai cũng xem được)
 */
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024L * 1024 * 500,
        maxRequestSize = 1024L * 1024 * 510)
@WebServlet(urlPatterns = { "/video", "/video/add", "/video/insert", "/video/edit",
        "/video/update", "/video/delete", "/video/detail" })
public class VideoController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> VIDEO_EXT = Set.of("mp4", "webm", "ogg");

    private final IVideoService videoService = new VideoServiceImpl();
    private final ICategoryService cateService = new CategoryServiceImpl();

    // ------------------------------------------------------------------ GET
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getServletPath();
        User user = currentUser(req);

        if (!"/video/detail".equals(path) && user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        switch (path) {
            case "/video/add":
                showForm(req, resp, null);
                break;
            case "/video/edit": {
                Video video = ownedVideo(req, user);
                if (video == null) {
                    resp.sendRedirect(req.getContextPath() + "/video");
                    return;
                }
                showForm(req, resp, video);
                break;
            }
            case "/video/detail":
                detail(req, resp, user);
                break;
            default: // "/video"
                list(req, resp, user);
        }
    }

    // ----------------------------------------------------------------- POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User user = currentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        switch (req.getServletPath()) {
            case "/video/insert":
                insert(req, resp, user);
                break;
            case "/video/update":
                update(req, resp, user);
                break;
            case "/video/delete":
                delete(req, resp, user);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/video");
        }
    }

    // ------------------------------------------------------------- handlers
    private void list(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        int pageSize = Constant.VIDEO_PAGE_SIZE;
        long total = videoService.countByUser(user.getId());
        int totalPages = (int) Math.ceil(total / (double) pageSize);

        int page = parseInt(req.getParameter("page"), 1);
        if (page < 1) page = 1;
        if (totalPages > 0 && page > totalPages) page = totalPages;

        List<Video> videos = videoService.findByUser(user.getId(), page - 1, pageSize);
        req.setAttribute("videos", videos);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("total", total);
        req.setAttribute("pageSize", pageSize);
        req.getRequestDispatcher("/views/video/video-list.jsp").forward(req, resp);
    }

    private void detail(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        Video video = (id == null) ? null : videoService.findById(id);
        boolean owner = video != null && user != null && video.getUser() != null
                && video.getUser().getId() == user.getId();
        if (video == null || (video.getActive() != 1 && !owner)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy video");
            return;
        }
        videoService.increaseViews(id);
        video.setViews(video.getViews() + 1);
        req.setAttribute("video", video);
        req.setAttribute("ytId", youtubeId(video.getVideoUrl()));   // ID YouTube (null nếu là file upload)
        req.getRequestDispatcher("/views/video/video-detail.jsp").forward(req, resp);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Video video)
            throws ServletException, IOException {
        List<Category> cates = cateService.findAll();
        req.setAttribute("listcate", cates);
        if (video != null) {
            req.setAttribute("video", video);
        }
        req.getRequestDispatcher("/views/video/video-form.jsp").forward(req, resp);
    }

    private void insert(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        Part posterPart = req.getPart("poster");
        Part videoPart = req.getPart("videoFile");
        String link = req.getParameter("videoLink");
        link = (link == null) ? "" : link.trim();

        // Bài 2: validation (Hibernate Validator + kiểm tra file / link)
        VideoForm form = buildVideoForm(req);
        Map<String, String> errors = ValidationUtil.validate(form);

        if (hasFile(videoPart)) {
            if (!extAllowed(videoPart, VIDEO_EXT)) {
                errors.put("videoFile", "Video chỉ chấp nhận định dạng: mp4, webm, ogg");
            }
        } else if (link.isEmpty()) {
            errors.put("videoFile", "Vui lòng nhập link YouTube hoặc chọn file video!");
        } else if (youtubeId(link) == null) {
            errors.put("videoLink", "Link YouTube không hợp lệ!");
        }
        if (hasFile(posterPart) && !extAllowed(posterPart, IMAGE_EXT)) {
            errors.put("poster", "Poster chỉ chấp nhận ảnh: jpg, jpeg, png, gif, webp");
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("form", form);
            showForm(req, resp, null);
            return;
        }

        Video video = new Video();
        fillCommonFields(req, video);
        video.setUser(user);
        video.setViews(0);
        video.setCreatedDate(new Date());
        if (hasFile(videoPart)) {
            video.setVideoUrl(saveFile(videoPart));              // tên file đã upload
        } else {
            video.setVideoUrl(youtubeUrl(youtubeId(link)));      // link YouTube chuẩn hóa
        }
        if (hasFile(posterPart)) {
            video.setPoster(saveFile(posterPart));
        }
        videoService.insert(video);
        resp.sendRedirect(req.getContextPath() + "/video");
    }

    private void update(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        Video video = ownedVideo(req, user);
        if (video == null) {
            resp.sendRedirect(req.getContextPath() + "/video");
            return;
        }
        Part posterPart = req.getPart("poster");
        Part videoPart = req.getPart("videoFile");
        String link = req.getParameter("videoLink");
        link = (link == null) ? "" : link.trim();

        // Bài 2: validation (Hibernate Validator + kiểm tra file / link)
        VideoForm form = buildVideoForm(req);
        Map<String, String> errors = ValidationUtil.validate(form);

        if (hasFile(videoPart)) {
            if (!extAllowed(videoPart, VIDEO_EXT)) {
                errors.put("videoFile", "Video chỉ chấp nhận định dạng: mp4, webm, ogg");
            }
        } else if (!link.isEmpty() && youtubeId(link) == null) {
            errors.put("videoLink", "Link YouTube không hợp lệ!");
        }
        if (hasFile(posterPart) && !extAllowed(posterPart, IMAGE_EXT)) {
            errors.put("poster", "Poster chỉ chấp nhận ảnh: jpg, jpeg, png, gif, webp");
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("form", form);
            showForm(req, resp, video);
            return;
        }

        fillCommonFields(req, video);

        if (hasFile(videoPart)) {                       // chọn file mới -> ưu tiên file
            deleteFile(video.getVideoUrl());            // file cũ (nếu là link http thì tự bỏ qua)
            video.setVideoUrl(saveFile(videoPart));
        } else if (!link.isEmpty()) {                   // dùng link YouTube
            String newUrl = youtubeUrl(youtubeId(link));
            if (!newUrl.equals(video.getVideoUrl())) {  // link đổi mới xử lý
                deleteFile(video.getVideoUrl());        // nếu trước đó là file upload thì xóa file đó
                video.setVideoUrl(newUrl);
            }
        }                                               // trống cả hai -> giữ nguyên

        if (hasFile(posterPart)) {                      // có chọn poster mới -> xóa file cũ
            deleteFile(video.getPoster());
            video.setPoster(saveFile(posterPart));
        }
        videoService.update(video);
        resp.sendRedirect(req.getContextPath() + "/video");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        Video video = ownedVideo(req, user);
        if (video != null) {
            try {
                videoService.delete(video.getVideoId());
                deleteFile(video.getVideoUrl());
                deleteFile(video.getPoster());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        resp.sendRedirect(req.getContextPath() + "/video");
    }

    // -------------------------------------------------------------- helpers
    private VideoForm buildVideoForm(HttpServletRequest req) {
        VideoForm f = new VideoForm();
        f.setTitle(req.getParameter("title") == null ? "" : req.getParameter("title").trim());
        f.setDescription(req.getParameter("description") == null ? "" : req.getParameter("description"));
        f.setCategoryId(parseInt(req.getParameter("categoryId"), 0));
        f.setActive("1".equals(req.getParameter("active")) ? 1 : 0);
        return f;
    }

    /** Đọc title / description / active / category từ form vào video. */
    private void fillCommonFields(HttpServletRequest req, Video video) {
        video.setTitle(req.getParameter("title").trim());
        video.setDescription(req.getParameter("description"));
        video.setActive("1".equals(req.getParameter("active")) ? 1 : 0);

        int cateId = parseInt(req.getParameter("categoryId"), 0);
        video.setCategory(cateId > 0 ? cateService.findById(cateId) : null);
    }

    private User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute(Constant.SESSION_ACCOUNT);
    }

    /** Lấy video theo ?id= hoặc field id, chỉ trả về nếu video đó thuộc user đang đăng nhập. */
    private Video ownedVideo(HttpServletRequest req, User user) {
        String id = req.getParameter("id");
        if (id == null || id.isEmpty()) return null;
        Video video = videoService.findById(id);
        if (video == null || video.getUser() == null || video.getUser().getId() != user.getId()) {
            return null;
        }
        return video;
    }

    private boolean hasFile(Part part) {
        return part != null && part.getSize() > 0
                && part.getSubmittedFileName() != null && !part.getSubmittedFileName().isEmpty();
    }

    private String extOf(Part part) {
        String name = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        int i = name.lastIndexOf('.');
        return i >= 0 ? name.substring(i + 1).toLowerCase() : "";
    }

    private boolean extAllowed(Part part, Set<String> allowed) {
        return allowed.contains(extOf(part));
    }

    /** Lưu file vào Constant.DIR với tên ngẫu nhiên (tránh trùng tên), trả về tên file. */
    private String saveFile(Part part) throws IOException {
        File dir = new File(Constant.DIR);
        if (!dir.exists()) dir.mkdirs();
        String fname = UUID.randomUUID() + "." + extOf(part);
        part.write(new File(dir, fname).getAbsolutePath());
        return fname;
    }

    private void deleteFile(String fname) throws IOException {
        if (fname != null && !fname.isEmpty() && !fname.startsWith("http")) {
            Files.deleteIfExists(Paths.get(Constant.DIR, Paths.get(fname).getFileName().toString()));
        }
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    // Hỗ trợ: youtube.com/watch?v=ID, youtu.be/ID, youtube.com/embed/ID, youtube.com/shorts/ID
    private static final Pattern YT_PATTERN = Pattern.compile(
        "^(?:https?://)?(?:www\\.|m\\.)?(?:youtube\\.com/(?:watch\\?(?:.*&)?v=|embed/|shorts/)|youtu\\.be/)([\\w-]{11})");

    /** Trả về ID 11 ký tự của video YouTube, hoặc null nếu không phải link YouTube hợp lệ. */
    private String youtubeId(String url) {
        if (url == null) return null;
        Matcher m = YT_PATTERN.matcher(url.trim());
        return m.find() ? m.group(1) : null;
    }

    private String youtubeUrl(String id) {
        return "https://www.youtube.com/watch?v=" + id;   // luôn lưu 1 dạng chuẩn
    }
}