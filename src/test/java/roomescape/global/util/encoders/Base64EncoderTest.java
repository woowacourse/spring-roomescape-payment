package roomescape.global.util.encoders;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.global.util.Encoder;

@DisplayName("Base64 인코더 테스트")
class Base64EncoderTest {

    @DisplayName("Base64 인코딩에 성공한다.")
    @Test
    public void encode() {
        //given
        Encoder encoder = new Base64Encoder();
        String key = "myKey";
        String expectedEncodedValue = "bXlLZXk6";

        //when
        String encodedValue = encoder.encode(key);

        //then
        assertThat(expectedEncodedValue).isEqualTo(encodedValue);
    }
}
