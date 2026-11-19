package vn.iotstar.util;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.Part;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Bài 2: validation phía server dùng Hibernate Validator. */
public final class ValidationUtil {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private ValidationUtil() {}

    /** Trả về Map(tên field -> thông báo lỗi). Map rỗng nghĩa là hợp lệ. */
    public static <T> Map<String, String> validate(T bean) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (ConstraintViolation<T> v : VALIDATOR.validate(bean)) {
            errors.putIfAbsent(v.getPropertyPath().toString(), v.getMessage());
        }
        return errors;
    }

    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    public static boolean hasFile(Part part) {
        return part != null && part.getSize() > 0
                && part.getSubmittedFileName() != null && !part.getSubmittedFileName().isEmpty();
    }

    public static String extOf(Part part) {
        String name = java.nio.file.Paths.get(part.getSubmittedFileName()).getFileName().toString();
        int i = name.lastIndexOf('.');
        return i >= 0 ? name.substring(i + 1).toLowerCase() : "";
    }
}
