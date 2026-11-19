package vn.iotstar.controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.form.OtpForm;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.ValidationUtil;

/** Kích hoạt tài khoản bằng OTP gửi qua email sau khi đăng ký. */
@WebServlet(urlPatterns = "/verify-otp")
public class VerifyOtpController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    UserService service = new UserServiceImpl();

    private String emailInSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (String) session.getAttribute(Constant.SESSION_OTP_EMAIL);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = emailInSession(req);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }
        req.setAttribute("email", email);
        req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email = emailInSession(req);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/register");
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
            req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
            return;
        }

        OtpForm form = new OtpForm();
        form.setOtp(ValidationUtil.trim(req.getParameter("otp")));
        Map<String, String> errors = ValidationUtil.validate(form);
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
            return;
        }

        if (service.activateAccount(email, form.getOtp())) {
            req.getSession().removeAttribute(Constant.SESSION_OTP_EMAIL);
            resp.sendRedirect(req.getContextPath() + "/login?activated=1");
        } else {
            req.setAttribute("alert", "OTP không đúng hoặc đã hết hạn!");
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
        }
    }
}
