package vn.iotstar.form;

import java.io.Serializable;

import jakarta.validation.constraints.*;

/** Dữ liệu form + quy tắc validation (Bean Validation / Hibernate Validator). */
public class ProfileForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên từ 2 đến 100 ký tự")
    @Pattern(regexp = "^[\\p{L} .\'-]*$", message = "Họ tên chỉ gồm chữ cái và khoảng trắng")
    private String fullname;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không hợp lệ (VD: 0912345678)")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    @Email(regexp = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$", message = "Email không hợp lệ")
    private String email;

    public ProfileForm() {}

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
