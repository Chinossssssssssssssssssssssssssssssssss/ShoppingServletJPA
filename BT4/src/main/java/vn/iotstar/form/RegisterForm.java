package vn.iotstar.form;

import java.io.Serializable;

import jakarta.validation.constraints.*;

/** Dữ liệu form + quy tắc validation (Bean Validation / Hibernate Validator). */
public class RegisterForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Tài khoản không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "Tài khoản 4-20 ký tự, chỉ gồm chữ, số và dấu _")
    private String username;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên từ 2 đến 100 ký tự")
    @Pattern(regexp = "^[\\p{L} .\'-]*$", message = "Họ tên chỉ gồm chữ cái và khoảng trắng")
    private String fullname;

    @NotBlank(message = "Email không được để trống")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    @Email(regexp = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$", message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không hợp lệ (VD: 0912345678)")
    private String phone;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9]).{8,50}$", message = "Mật khẩu 8-50 ký tự, gồm cả chữ và số")
    private String password;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu")
    private String confirmPassword;

    public RegisterForm() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
