package vn.iotstar.form;

import java.io.Serializable;

import jakarta.validation.constraints.*;

/** Dữ liệu form + quy tắc validation (Bean Validation / Hibernate Validator). */
public class ForgotForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Email không được để trống")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    @Email(regexp = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$", message = "Email không hợp lệ")
    private String email;

    public ForgotForm() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
