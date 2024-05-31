package roomescape.learningtest.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import roomescape.exception.PaymentException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TossApiTest {

    private final TossExceptionApi tossExceptionApi = new TossExceptionApi();

    @DisplayName("500번대 에러가 발생했을때의 처리 과정을 확인한다.")
    @Test
    void exceptionOf500Test() {
        assertThatThrownBy(() -> tossExceptionApi.raiseExceptionOf("FAILED_INTERNAL_SYSTEM_PROCESSING"))
                .isInstanceOf(PaymentException.class);
    }
}
