package vn.iotstar.util;

import java.security.SecureRandom;

public final class OtpUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private OtpUtil() {}

    /** Sinh mã OTP 6 chữ số (giữ số 0 ở đầu, ví dụ 004821). */
    public static String generate() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}
