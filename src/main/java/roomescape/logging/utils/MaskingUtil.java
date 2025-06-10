package roomescape.logging.utils;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class MaskingUtil {

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String id = parts[0];
        if (id.length() <= 2) {
            id = id.charAt(0) + "*";
        } else {
            id = id.substring(0, 2) + "*".repeat(id.length() - 2);
        }
        return id + "@" + parts[1];
    }
}
