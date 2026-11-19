package vn.iotstar.controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.form.RegisterForm;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.ValidationUtil;

@WebServlet(urlPatterns = "/register")
public class RegisterController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }
        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        RegisterForm form = new RegisterForm();
        form.setUsername(ValidationUtil.trim(req.getParameter("username")));
        form.setFullname(ValidationUtil.trim(req.getParameter("fullname")));
        form.setEmail(ValidationUtil.trim(req.getParameter("email")));
        form.setPhone(ValidationUtil.trim(req.getParameter("phone")));
        form.setPassword(req.getParameter("password") == null ? "" : req.getParameter("password"));
        form.setConfirmPassword(req.getParameter("confirmPassword") == null ? "" : req.getParameter("confirmPassword"));

        // Bài 2: validation định dạng
        Map<String, String> errors = ValidationUtil.validate(form);
        if (!errors.containsKey("confirmPassword") && !form.getPassword().equals(form.getConfirmPassword())) {
            errors.put("confirmPassword", "Mật khẩu nhập lại không khớp");
        }
        // Kiểm tra trùng trong DB (chỉ khi định dạng đã đúng)
        if (!errors.containsKey("email") && service.checkExistEmail(form.getEmail())) {
            errors.put("email", "Email đã tồn tại!");
        }
        if (!errors.containsKey("username") && service.checkExistUsername(form.getUsername())) {
            errors.put("username", "Tài khoản đã tồn tại!");
        }
        if (!errors.containsKey("phone") && service.checkExistPhone(form.getPhone())) {
            errors.put("phone", "Số điện thoại đã tồn tại!");
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
            return;
        }

        String email = form.getEmail();
        boolean isSuccess = service.register(form.getUsername(), form.getPassword(), email,
                form.getFullname(), form.getPhone());
        if (!isSuccess) {
            req.setAttribute("alert", "System error!");
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
            return;
        }

        // Tài khoản đã tạo nhưng chưa kích hoạt -> gửi OTP rồi chuyển sang trang nhập OTP
        req.getSession(true).setAttribute(Constant.SESSION_OTP_EMAIL, email);
        try {
            service.sendOtp(email);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("email", email);
            req.setAttribute("alert", "Tạo tài khoản thành công nhưng chưa gửi được email OTP. Hãy bấm \"Gửi lại OTP\".");
            req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/verify-otp");
    }
}
