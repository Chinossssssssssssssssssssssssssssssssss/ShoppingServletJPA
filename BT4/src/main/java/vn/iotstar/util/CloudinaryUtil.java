package vn.iotstar.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.http.Part;

/**
 * Bài 3: upload ảnh lên Cloudinary.
 * KHÔNG hard-code khoá rồi push GitHub. Đặt biến môi trường (hoặc -D trong VM arguments):
 *   CLOUDINARY_URL=cloudinary://API_KEY:API_SECRET@CLOUD_NAME
 * hoặc 3 biến: CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET
 */
public final class CloudinaryUtil {
    private static Cloudinary instance;

    private CloudinaryUtil() {}

    private static String conf(String key) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) v = System.getProperty(key);
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private static synchronized Cloudinary get() {
        if (instance == null) {
            String url = conf("CLOUDINARY_URL");
            if (url != null) {
                instance = new Cloudinary(url);
            } else {
                String cloud = conf("CLOUDINARY_CLOUD_NAME");
                String key = conf("CLOUDINARY_API_KEY");
                String secret = conf("CLOUDINARY_API_SECRET");
                if (cloud == null || key == null || secret == null) {
                    throw new IllegalStateException("Chưa cấu hình Cloudinary (CLOUDINARY_CLOUD_NAME / "
                            + "CLOUDINARY_API_KEY / CLOUDINARY_API_SECRET hoặc CLOUDINARY_URL)");
                }
                instance = new Cloudinary(ObjectUtils.asMap(
                        "cloud_name", cloud, "api_key", key, "api_secret", secret, "secure", true));
            }
        }
        return instance;
    }

    /** Upload 1 ảnh từ multipart, trả về URL https (secure_url). */
    @SuppressWarnings("rawtypes")
    public static String uploadImage(Part part) throws IOException {
        Cloudinary cloud = get(); // kiểm tra cấu hình trước khi đọc file
        Path tmp = Files.createTempFile("avatar-", "." + ValidationUtil.extOf(part));
        try {
            try (InputStream in = part.getInputStream()) {
                Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
            }
            Map result = cloud.uploader().upload(tmp.toFile(),
                    ObjectUtils.asMap("folder", "avatars", "resource_type", "image"));
            Object url = result.get("secure_url");
            if (url == null) throw new IOException("Cloudinary không trả về URL");
            return url.toString();
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
