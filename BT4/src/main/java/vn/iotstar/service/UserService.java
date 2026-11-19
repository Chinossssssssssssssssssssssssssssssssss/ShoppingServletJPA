package vn.iotstar.service;

import vn.iotstar.entity.User;

public interface UserService {
    User login(String username, String password);
    User get(String username);
    User getByEmail(String email);
    void insert(User user);

    /** Tạo tài khoản CHƯA kích hoạt, mật khẩu được hash. Chưa gửi OTP. */
    boolean register(String username, String password, String email, String fullname, String phone);

    /** Sinh OTP mới, lưu vào DB (hạn 5 phút) và gửi qua email. Ném RuntimeException nếu gửi mail lỗi. */
    void sendOtp(String email);

    /** Kiểm tra OTP đăng ký, đúng thì kích hoạt tài khoản. */
    boolean activateAccount(String email, String otp);

    /** Kiểm tra OTP quên mật khẩu, đúng thì đổi sang mật khẩu mới (được hash). */
    boolean resetPassword(String email, String otp, String newPassword);

    User getById(int id);

    /** Bài 3: cập nhật hồ sơ (fullname, phone, email, avatar). avatar null/rỗng = giữ ảnh cũ. Trả về user mới. */
    User updateProfile(int id, String fullName, String phone, String email, String avatar);

    boolean checkExistEmail(String email);
    boolean checkExistUsername(String username);
    boolean checkExistPhone(String phone);
}
