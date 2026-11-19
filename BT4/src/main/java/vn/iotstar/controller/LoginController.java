package vn.iotstar.controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.form.LoginForm;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.ValidationUtil;

@WebServlet(urlPatterns = "/login")
public class LoginController extends HttpServlet {
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
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(Constant.COOKIE_REMEMBER)) {
                    User user = service.get(c.getValue());
                    if (user != null && user.isActivated()) {
                        req.getSession(true).setAttribute(Constant.SESSION_ACCOUNT, user);
                        resp.sendRedirect(req.getContextPath() + "/waiting");
                        return;
                    }
                }
            }
        }
        req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        LoginForm form = new LoginForm();
        form.setUsername(ValidationUtil.trim(req.getParameter("username")));
        form.setPassword(req.getParameter("password") == null ? "" : req.getParameter("password"));
        boolean isRememberMe = "on".equals(req.getParameter("remember"));

        // Bài 2: validation
        Map<String, String> errors = ValidationUtil.validate(form);
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
            return;
        }

        String username = form.getUsername();
        User user = service.login(username, form.getPassword());
        if (user != null && !user.isActivated()) {
            // Đúng mật khẩu nhưng chưa kích hoạt bằng OTP
            req.getSession(true).setAttribute(Constant.SESSION_OTP_EMAIL, user.getEmail());
            req.setAttribute("alert", "Tài khoản chưa được kích hoạt.");
            req.setAttribute("needActivate", true);
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
            return;
        }
        if (user != null) {
            req.getSession(true).setAttribute(Constant.SESSION_ACCOUNT, user);
            if (isRememberMe) {
                saveRememberMe(resp, username);
            }
            resp.sendRedirect(req.getContextPath() + "/waiting");
        } else {
            req.setAttribute("alert", "Tài khoản hoặc mật khẩu không đúng");
            req.setAttribute("form", form);
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
        }
    }

    private void saveRememberMe(HttpServletResponse resp, String username) {
        Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, username);
        cookie.setMaxAge(30 * 60);
        resp.addCookie(cookie);
    }
}
