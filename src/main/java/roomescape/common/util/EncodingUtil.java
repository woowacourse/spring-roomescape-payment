package roomescape.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncodingUtil {

    public static String encodeBase64(String value) {
        return Base64.getEncoder()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeBase64(String base64Encoded) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Encoded);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    private EncodingUtil() {
        throw new UnsupportedOperationException("[ERROR] 유틸리티 클래스는 인스턴스를 생성할 수 없습니다.");
    }
}
