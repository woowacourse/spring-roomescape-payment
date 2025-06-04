package roomescape.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRequest;

@Disabled
@SpringBootTest
@DisplayName("[실제 토스 페이먼츠] API 테스트")
public class TossPaymentProviderRealTest {

    @Value("${api.toss.base-url}")
    private String tossBaseUrl;
    @Value("${api.toss.confirm-uri}")
    private String tossConfirmUri;

    @Autowired
    private RestTemplate restTemplate;

    private final PaymentRequest junkRequest = new PaymentRequest("abcd", "xyz", 9999);

    @Test
    @DisplayName("토스 API와 연결에 성공한다.")
    void connect() {
        assertThatThrownBy(() -> restTemplate.postForLocation(tossBaseUrl + tossConfirmUri, junkRequest))

            .as("토스 API와 HTTP 연결에 성공한 경우 스프링 I/O 예외인 ResourceAccessException이 발생하지 않아야한다.")
            .isNotInstanceOf(ResourceAccessException.class);
    }

    @Test
    @DisplayName("토스 API 스펙에 맞게 요청을 보낸다.")
    void request() {
        assertThatThrownBy(() -> restTemplate.postForEntity(tossBaseUrl + tossConfirmUri, junkRequest, Payment.class))

            .as("토스에서 API 스펙이 맞지 않는 경우 '필수 파라미터가 누락되었습니다.' 라는 메시지를 응답한다.")
            .hasMessageNotContainingAny("필수", "파라미터", "누락");
    }
}
