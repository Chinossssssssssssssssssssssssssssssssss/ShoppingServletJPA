package vn.iotstar.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category;
import vn.iotstar.form.CategoryForm;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.ValidationUtil;

@MultipartConfig(maxFileSize = 10L * 1024 * 1024, maxRequestSize = 12L * 1024 * 1024)
@WebServlet(urlPatterns = { "/admin/categories", "/admin/category/add", "/admin/category/insert",
        "/admin/category/edit", "/admin/category/update", "/admin/category/delete" })
public class CategoryController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private final ICategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String url = req.getRequestURI();

        if (url.contains("/admin/categories")) {
            List<Category> list = cateService.findAll();
            req.setAttribute("listcate", list);
            req.getRequestDispatcher("/views/admin/category-list.jsp").forward(req, resp);

        } else if (url.contains("/admin/category/add")) {
            req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);

        } else if (url.contains("/admin/category/edit")) {
            int id = Integer.parseInt(req.getParameter("id"));
            Category category = cateService.findById(id);
            req.setAttribute("cate", category);
            req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);

        } else if (url.contains("/admin/category/delete")) {
            int id = Integer.parseInt(req.getParameter("id"));
            try {
                cateService.delete(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String url = req.getRequestURI();

        File uploadDir = new File(Constant.DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        if (url.contains("/admin/category/insert")) {
            CategoryForm form = buildForm(req);
            Part part = req.getPart("images1");
            Map<String, String> errors = ValidationUtil.validate(form);
            checkImage(part, errors);
            if (!errors.isEmpty()) {
                req.setAttribute("errors", errors);
                req.setAttribute("form", form);
                req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
                return;
            }

            Category category = new Category();
            category.setCategoryname(form.getCategoryname());
            category.setStatus(form.getStatus());
            if (ValidationUtil.hasFile(part)) {
                category.setImages(saveFile(part));
            } else if (!form.getImages().isEmpty()) {
                category.setImages(form.getImages());
            } else {
                category.setImages("avatar.jpg");
            }
            cateService.insert(category);
            resp.sendRedirect(req.getContextPath() + "/admin/categories");

        } else if (url.contains("/admin/category/update")) {
            int categoryid = Integer.parseInt(req.getParameter("categoryid"));
            Category category = cateService.findById(categoryid);
            if (category == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }
            CategoryForm form = buildForm(req);
            Part part = req.getPart("images1");
            Map<String, String> errors = ValidationUtil.validate(form);
            checkImage(part, errors);
            if (!errors.isEmpty()) {
                req.setAttribute("errors", errors);
                req.setAttribute("form", form);
                req.setAttribute("cate", category);
                req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
                return;
            }

            String fileold = category.getImages();
            category.setCategoryname(form.getCategoryname());
            category.setStatus(form.getStatus());
            if (ValidationUtil.hasFile(part)) {
                // xóa file cũ nếu là file upload (không phải link http)
                if (fileold != null && !fileold.startsWith("http")) {
                    Files.deleteIfExists(Paths.get(Constant.DIR, fileold));
                }
                category.setImages(saveFile(part));
            } else if (!form.getImages().isEmpty()) {
                category.setImages(form.getImages());
            } else {
                category.setImages(fileold);
            }
            cateService.update(category);
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
    }

    private CategoryForm buildForm(HttpServletRequest req) {
        CategoryForm form = new CategoryForm();
        form.setCategoryname(ValidationUtil.trim(req.getParameter("categoryname")));
        form.setImages(ValidationUtil.trim(req.getParameter("images")));
        try {
            form.setStatus(Integer.valueOf(req.getParameter("status")));
        } catch (NumberFormatException e) {
            form.setStatus(null); // @NotNull sẽ báo lỗi
        }
        return form;
    }

    private void checkImage(Part part, Map<String, String> errors) {
        if (ValidationUtil.hasFile(part) && !IMAGE_EXT.contains(ValidationUtil.extOf(part))) {
            errors.put("images1", "Chỉ chấp nhận ảnh: jpg, jpeg, png, gif, webp");
        }
    }

    // Lưu file upload, trả về tên file mới
    private String saveFile(Part part) throws IOException {
        String ext = ValidationUtil.extOf(part);
        if (ext.isEmpty()) ext = "png";
        String fname = System.currentTimeMillis() + "." + ext;
        part.write(Constant.DIR + "/" + fname);
        return fname;
    }
}
