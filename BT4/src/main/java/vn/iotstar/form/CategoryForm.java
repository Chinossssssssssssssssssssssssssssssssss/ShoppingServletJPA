package vn.iotstar.form;

import java.io.Serializable;

import jakarta.validation.constraints.*;

/** Dữ liệu form + quy tắc validation (Bean Validation / Hibernate Validator). */
public class CategoryForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Tên category không được để trống")
    @Size(max = 255, message = "Tên category tối đa 255 ký tự")
    private String categoryname;

    @Size(max = 500, message = "Link ảnh tối đa 500 ký tự")
    @Pattern(regexp = "^$|^https?://\\S+$", message = "Link ảnh phải bắt đầu bằng http:// hoặc https://")
    private String images;

    @NotNull(message = "Trạng thái không hợp lệ")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 1, message = "Trạng thái không hợp lệ")
    private Integer status;

    public CategoryForm() {}

    public String getCategoryname() { return categoryname; }
    public void setCategoryname(String categoryname) { this.categoryname = categoryname; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
