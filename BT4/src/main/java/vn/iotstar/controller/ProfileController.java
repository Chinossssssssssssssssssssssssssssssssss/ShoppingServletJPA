package vn.iotstar.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.User;
import vn.iotstar.form.OtpForm;
import vn.iotstar.form.PendingProfile;
import vn.iotstar.form.ProfileForm;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.CloudinaryUtil;
import vn.iotstar.util.Constant;
import vn.iotstar.util.EmailUtil;
import vn.iotstar.util.OtpUtil;
import vn.iotstar.util.ValidationUtil;

/**
 * Bài 3: Profile của user.
 *   GET  /profile          : form cập nhật (fullname, phone, email, avatar)
 *   POST /profile          : lưu. Email KHÔNG đổi -> cập nhật ngay.
 *                            Email ĐỔI -> gửi OTP tới email mới, chờ xác nhận ở /profile/verify
 *   GET  /profile/verify   : form nhập OTP
 *   POST /profile/verify   : OTP đúng -> cập nhật DB (hoặc gửi lại OTP / hủy)
 * Ảnh avatar upload bằng multipart rồi đẩy lên Cloudinary, DB chỉ lưu URL.
 */
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5L * 1024 * 1024, maxRequestSize = 6L * 1024 * 1024)
@WebServlet(urlPatterns = { "/profile", "/profile/verify" })
public class ProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final String PENDING = "pendingProfile";
    private static final int MAX_ATTEMPTS = 5;
    private static final String PROFILE_JSP = "/views/profile.jsp";
    private static final String VERIFY_JSP = "/views/profile-verify.jsp";

    private final UserService service = new UserServiceImpl();

    private User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute(Constant.SESSION_ACCOUNT);
    }

    private PendingProfile pending(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (PendingProfile) session.getAttribute(PENDING);
    }

    /** Lấy bản mới nhất từ DB và cập nhật lại session. */
    private User refresh(HttpServletRequest req, User account) {
        User fresh = service.getById(account.getId());
        if (fresh != null) {
            req.getSession().setAttribute(Constant.SESSION_ACCOUNT, fresh);
            return fresh;
        }
        return account;
    }

    // ------------------------------------------------------------------ GET
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        User account = currentUser(req);
        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if ("/profile/verify".equals(req.getServletPath())) {
            PendingProfile p = pending(req);
            if (p == null) {
                resp.sendRedirect(req.getContextPath() + "/profile");
                return;
            }
            req.setAttribute("newEmail", p.getEmail());
            req.getRequestDispatcher(VERIFY_JSP).forward(req, resp);
            return;
        }

        refresh(req, account);
        req.getRequestDispatcher(PROFILE_JSP).forward(req, resp);
    }

    // ----------------------------------------------------------------- POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        User account = currentUser(req);
        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if ("/profile/verify".equals(req.getServletPath())) {
            verify(req, resp, account);
        } else {
            save(req, resp, account);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, User account)
            throws ServletException, IOException {
        account = refresh(req, account);

        ProfileForm form = new ProfileForm();
        form.setFullname(ValidationUtil.trim(req.getParameter("fullname")));
        form.setPhone(ValidationUtil.trim(req.getParameter("phone")));
        form.setEmail(ValidationUtil.trim(req.getParameter("email")));

        // 1. validation định dạng
        Map<String, String> errors = ValidationUtil.validate(form);

        // 2. kiểm tra trùng (chỉ khi giá trị thay đổi)
        boolean emailChanged = account.getEmail() == null || !form.getEmail().equalsIgnoreCase(account.getEmail());
        if (!errors.containsKey("phone") && !form.getPhone().equals(account.getPhone())
                && service.checkExistPhone(form.getPhone())) {
            errors.put("phone", "Số điện thoại đã được sử dụng!");
        }
        if (!errors.containsKey("email") && emailChanged && service.checkExistEmail(form.getEmail())) {
            errors.put("email", "Email đã được sử dụng!");
        }

        // 3. kiểm tra file avatar
        Part part = null;
        try {
            part = req.getPart("avatar");
        } catch (IllegalStateException e) { // vượt maxFileSize / maxRequestSize
            errors.put("avatar", "Ảnh quá lớn (tối đa 5MB)");
        }
        boolean hasAvatar = ValidationUtil.hasFile(part);
        if (hasAvatar) {
            String ct = part.getContentType();
            if (!IMAGE_EXT.contains(ValidationUtil.extOf(part)) || ct == null || !ct.startsWith("image/")) {
                errors.put("avatar", "Chỉ chấp nhận ảnh: jpg, jpeg, png, gif, webp");
            }
        }
        if (!errors.isEmpty()) {
            backToProfile(req, resp, form, errors, null);
            return;
        }

        // 4. upload avatar lên Cloudinary (nếu có chọn ảnh)
        String avatarUrl = null;
        if (hasAvatar) {
            try {
                avatarUrl = CloudinaryUtil.uploadImage(part);
            } catch (Exception e) {
                e.printStackTrace();
                errors.put("avatar", "Upload ảnh thất bại: " + e.getMessage());
                backToProfile(req, resp, form, errors, null);
                return;
            }
        }

        // 5a. email không đổi -> cập nhật luôn
        if (!emailChanged) {
            User updated = service.updateProfile(account.getId(), form.getFullname(), form.getPhone(),
                    account.getEmail(), avatarUrl);
            req.getSession().setAttribute(Constant.SESSION_ACCOUNT, updated);
            resp.sendRedirect(req.getContextPath() + "/profile?success=1");
            return;
        }

        // 5b. email mới -> gửi OTP tới email mới, chưa ghi gì vào DB
        String otp = OtpUtil.generate();
        PendingProfile p = new PendingProfile(form.getFullname(), form.getPhone(), form.getEmail(), avatarUrl,
                otp, LocalDateTime.now().plusMinutes(Constant.OTP_EXPIRE_MINUTES));
        try {
            sendOtpMail(p.getEmail(), p.getFullName(), otp);
        } catch (Exception e) {
            e.printStackTrace();
            backToProfile(req, resp, form, null, "Không gửi được email OTP: " + e.getMessage());
            return;
        }
        req.getSession().setAttribute(PENDING, p);
        resp.sendRedirect(req.getContextPath() + "/profile/verify");
    }

    private void verify(HttpServletRequest req, HttpServletResponse resp, User account)
            throws ServletException, IOException {
        PendingProfile p = pending(req);
        if (p == null) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }
        req.setAttribute("newEmail", p.getEmail());
        String action = req.getParameter("action");

        if ("cancel".equals(action)) {
            req.getSession().removeAttribute(PENDING);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if ("resend".equals(action)) {
            String otp = OtpUtil.generate();
            try {
                sendOtpMail(p.getEmail(), p.getFullName(), otp);
                p.renew(otp, LocalDateTime.now().plusMinutes(Constant.OTP_EXPIRE_MINUTES));
                req.getSession().setAttribute(PENDING, p);
                req.setAttribute("info", "Đã gửi lại mã OTP tới " + p.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("alert", "Không gửi được email OTP: " + e.getMessage());
            }
            req.getRequestDispatcher(VERIFY_JSP).forward(req, resp);
            return;
        }

        OtpForm form = new OtpForm();
        form.setOtp(ValidationUtil.trim(req.getParameter("otp")));
        Map<String, String> errors = ValidationUtil.validate(form);
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("form", form);
            req.getRequestDispatcher(VERIFY_JSP).forward(req, resp);
            return;
        }

        if (p.isExpired()) {
            req.setAttribute("alert", "OTP đã hết hạn, hãy bấm \"Gửi lại OTP\".");
        } else if (!p.getOtp().equals(form.getOtp())) {
            p.failed();
            if (p.getAttempts() >= MAX_ATTEMPTS) {
                req.getSession().removeAttribute(PENDING);
                resp.sendRedirect(req.getContextPath() + "/profile?locked=1");
                return;
            }
            req.setAttribute("alert", "OTP không đúng! (còn " + (MAX_ATTEMPTS - p.getAttempts()) + " lần thử)");
        } else if (service.checkExistEmail(p.getEmail())) {
            // trong lúc chờ OTP, email này đã bị người khác dùng
            req.getSession().removeAttribute(PENDING);
            resp.sendRedirect(req.getContextPath() + "/profile?taken=1");
            return;
        } else {
            User updated = service.updateProfile(account.getId(), p.getFullName(), p.getPhone(),
                    p.getEmail(), p.getAvatar());
            req.getSession().setAttribute(Constant.SESSION_ACCOUNT, updated);
            req.getSession().removeAttribute(PENDING);
            resp.sendRedirect(req.getContextPath() + "/profile?success=1");
            return;
        }
        req.setAttribute("form", form);
        req.getRequestDispatcher(VERIFY_JSP).forward(req, resp);
    }

    private void backToProfile(HttpServletRequest req, HttpServletResponse resp, ProfileForm form,
                               Map<String, String> errors, String alert) throws ServletException, IOException {
        if (errors != null) req.setAttribute("errors", errors);
        if (alert != null) req.setAttribute("alert", alert);
        req.setAttribute("form", form);
        req.getRequestDispatcher(PROFILE_JSP).forward(req, resp);
    }

    private void sendOtpMail(String to, String fullName, String otp) throws Exception {
        EmailUtil.send(to, "Xác nhận email mới",
                "Xin chào " + fullName + ",\n\n"
              + "Mã OTP xác nhận đổi email của bạn là: " + otp + "\n"
              + "Mã có hiệu lực trong " + Constant.OTP_EXPIRE_MINUTES + " phút. "
              + "Nếu không phải bạn thực hiện, hãy bỏ qua email này.");
    }
}
