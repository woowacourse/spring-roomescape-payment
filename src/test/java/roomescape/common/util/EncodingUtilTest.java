package roomescape.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EncodingUtilTest {

    @Test
    void encodeBase64_returnsEncodedString() {
        String original = "testString123!@#";
        String encoded = EncodingUtil.encodeBase64(original);

        assertThat(encoded).isEqualTo("dGVzdFN0cmluZzEyMyFAIw==");
    }

    @Test
    void decodeBase64_returnsOriginalString() {
        String base64 = "dGVzdFN0cmluZzEyMyFAIw==";
        String decoded = EncodingUtil.decodeBase64(base64);

        assertThat(decoded).isEqualTo("testString123!@#");
    }

    @Test
    void decodeBase64_withInvalidInput_throwsException() {
        assertThatThrownBy(() -> EncodingUtil.decodeBase64("invalid_base64"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> {
            var constructor = EncodingUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("[ERROR] 유틸리티 클래스는 인스턴스를 생성할 수 없습니다.");
    }
}
