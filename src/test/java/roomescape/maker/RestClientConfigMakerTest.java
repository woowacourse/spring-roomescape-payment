package roomescape.maker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.properties.TestProperties;

class RestClientConfigMakerTest {

    @DisplayName("설정 값들에 문제가 없을 경우 정상적으로 RestClient를 만든다.")
    @Test
    void makeRestClient() {
        assertDoesNotThrow(() -> new RestClientConfigMaker(new TestProperties()).makeRestClient());
    }
}
