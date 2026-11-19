package vn.iotstar.util;

public class Constant {
    public static final String SESSION_ACCOUNT = "account";
    public static final String SESSION_OTP_EMAIL = "otpEmail";
    public static final String COOKIE_REMEMBER = "username";
    public static final String DIR = "C:/upload";

    // ===== Cấu hình gửi mail (Gmail SMTP) =====
    // KHÔNG hard-code mật khẩu rồi push lên GitHub: đặt biến môi trường MAIL_USER / MAIL_PASS
    // (MAIL_PASS là "Mật khẩu ứng dụng" 16 ký tự của Gmail, không phải mật khẩu đăng nhập)
    public static final String MAIL_HOST = "smtp.gmail.com";
    public static final String MAIL_PORT = "587";
    public static final String MAIL_USER = env("MAIL_USER", "your_email@gmail.com");
    public static final String MAIL_PASS = env("MAIL_PASS", "your_app_password");

    public static final int OTP_EXPIRE_MINUTES = 5;
    public static final int VIDEO_PAGE_SIZE = 6;
    public static final int HOME_VIDEO_LIMIT = 10;

    private static String env(String key, String defaultValue) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? defaultValue : v;
    }

    public static class Path {
        public static final String LOGIN = "/views/login.jsp";
        public static final String REGISTER = "/views/register.jsp";
        public static final String VERIFY_OTP = "/views/verify-otp.jsp";
        public static final String FORGOT_PASSWORD = "/views/forgot-password.jsp";
        public static final String RESET_PASSWORD = "/views/reset-password.jsp";
    }
}
