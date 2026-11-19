package vn.iotstar.form;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Bài 3: thay đổi hồ sơ đang chờ xác nhận OTP (lưu trong session cho tới khi OTP đúng). */
public class PendingProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String fullName;
    private final String phone;
    private final String email;   // email MỚI
    private final String avatar;  // URL Cloudinary (có thể null nếu không đổi ảnh)
    private String otp;
    private LocalDateTime expiry;
    private int attempts;

    public PendingProfile(String fullName, String phone, String email, String avatar,
                          String otp, LocalDateTime expiry) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.avatar = avatar;
        this.otp = otp;
        this.expiry = expiry;
    }

    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAvatar() { return avatar; }
    public String getOtp() { return otp; }
    public LocalDateTime getExpiry() { return expiry; }
    public int getAttempts() { return attempts; }

    public void renew(String newOtp, LocalDateTime newExpiry) {
        this.otp = newOtp;
        this.expiry = newExpiry;
        this.attempts = 0;
    }
    public void failed() { attempts++; }
    public boolean isExpired() { return LocalDateTime.now().isAfter(expiry); }
}
