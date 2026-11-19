package vn.iotstar.controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.form.ResetForm;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.ValidationUtil;

/** Quên mật khẩu - bước 2: nhập OTP + mật khẩu mới. */
@WebServlet(urlPatterns = "/reset-password")
public class ResetPasswordController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    UserService service = new UserServiceImpl();

    private String emailInSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (String) session.getAttribute("resetEmail");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = emailInSession(req);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }
        req.setAttribute("email", email);
        req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email = emailInSession(req);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }
        req.setAttribute("email", email);

        if ("resend".equals(req.getParameter("action"))) {
            try {
                service.sendOtp(email);
                req.setAttribute("info", "Đã gửi lại mã OTP, vui lòng kiểm tra email.");
            } catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("alert", "Không gửi được email OTP: " + e.getMessage());
            }
            req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
            return;
        }

        ResetForm form = new ResetForm();
        form.setOtp(ValidationUtil.trim(req.getParameter("otp")));
        form.setPassword(req.getParameter("password") == null ? "" : req.getParameter("password"));
        form.setConfirm(req.getParameter("confirm") == null ? "" : req.getParameter("confirm"));

        // Bài 2: validation
        Map<String, String> errors = ValidationUtil.validate(form);
        if (!errors.containsKey("confirm") && !form.getPassword().equals(form.getConfirm())) {
            errors.put("confirm", "Mật khẩu nhập lại không khớp");
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
            return;
        }

        if (!service.resetPassword(email, form.getOtp(), form.getPassword())) {
            req.setAttribute("alert", "OTP không đúng hoặc đã hết hạn!");
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
            return;
        }

        req.getSession().removeAttribute("resetEmail");
        resp.sendRedirect(req.getContextPath() + "/login?reset=1");
    }
}
