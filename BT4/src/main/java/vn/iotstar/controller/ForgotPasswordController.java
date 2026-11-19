package vn.iotstar.controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.entity.User;
import vn.iotstar.form.ForgotForm;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.ValidationUtil;

/** Quên mật khẩu - bước 1: nhập email -> gửi OTP. */
@WebServlet(urlPatterns = "/forgot-password")
public class ForgotPasswordController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        ForgotForm form = new ForgotForm();
        form.setEmail(ValidationUtil.trim(req.getParameter("email")));

        // Bài 2: validation
        Map<String, String> errors = ValidationUtil.validate(form);
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
            return;
        }

        User user = service.getByEmail(form.getEmail());
        if (user == null) {
            errors.put("email", "Email không tồn tại trong hệ thống!");
            req.setAttribute("errors", errors);
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
            return;
        }
        if (!user.isActivated()) {
            req.getSession(true).setAttribute(Constant.SESSION_OTP_EMAIL, user.getEmail());
            req.setAttribute("alert", "Tài khoản chưa được kích hoạt. Hãy kích hoạt bằng OTP trước.");
            req.setAttribute("needActivate", true);
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
            return;
        }

        try {
            service.sendOtp(user.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("alert", "Không gửi được email OTP: " + e.getMessage());
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
            return;
        }

        // Lưu email vào session (riêng với session đăng nhập) để bước 2 biết đang reset cho ai
        req.getSession(true).setAttribute("resetEmail", user.getEmail());
        resp.sendRedirect(req.getContextPath() + "/reset-password");
    }
}
