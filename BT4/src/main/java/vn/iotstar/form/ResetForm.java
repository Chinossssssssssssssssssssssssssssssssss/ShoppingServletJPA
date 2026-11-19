package vn.iotstar.form;

import java.io.Serializable;

import jakarta.validation.constraints.*;

/** Dữ liệu form + quy tắc validation (Bean Validation / Hibernate Validator). */
public class ResetForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Vui lòng nhập mã OTP")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP gồm đúng 6 chữ số")
    private String otp;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9]).{8,50}$", message = "Mật khẩu 8-50 ký tự, gồm cả chữ và số")
    private String password;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu mới")
    private String confirm;

    public ResetForm() {}

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirm() { return confirm; }
    public void setConfirm(String confirm) { this.confirm = confirm; }
}
