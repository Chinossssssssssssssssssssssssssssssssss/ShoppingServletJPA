package vn.iotstar.form;

import java.io.Serializable;

import jakarta.validation.constraints.*;

/** Dữ liệu form + quy tắc validation (Bean Validation / Hibernate Validator). */
public class OtpForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Vui lòng nhập mã OTP")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP gồm đúng 6 chữ số")
    private String otp;

    public OtpForm() {}

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
