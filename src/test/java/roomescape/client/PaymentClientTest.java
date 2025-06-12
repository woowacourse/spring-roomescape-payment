package roomescape.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import roomescape.config.RestClientConfiguration;
import roomescape.dto.reservation.TossPaymentRequestDto;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;
import roomescape.exception.common.BadRequestException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RestClientConfiguration.class, ObjectMapper.class})
public class PaymentClientTest {

    @Autowired
    private RestClient.Builder restClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentClient paymentClient;

    private MockRestServiceServer mockRestServiceServer;

    @BeforeEach
    void setUp() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        RestClient testRestClient = restClientBuilder.build();
        paymentClient = new PaymentClient(objectMapper, testRestClient);
    }

    @DisplayName("잘못된 시크릿키로 결제 승인 요청시 500에러가 반환된다.")
    @Test
    void paymentExceptionTest() {

        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest()
                        .body("""
                                {
                                    "code":"INVALID_API_KEY",
                                    "message":"잘못된 시크릿키 연동 정보 입니다."
                                }
                        """)
                        .contentType(MediaType.APPLICATION_JSON));


        assertThatThrownBy(
                () -> paymentClient.confirmPayment(new TossPaymentRequestDto(
                        "paymentKey", "orderId", 1000L
                ))
        ).isInstanceOf(PaymentConfirmServerException.class);

        mockRestServiceServer.verify();
    }

    @DisplayName("paymentkey를 클라이언트에서 획득하지 않은 값으로 요청 시 400에러가 반환된다.")
    @Test
    void invalidPaymentKeyExceptionTest() {

        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withResourceNotFound()
                        .body("""
                                {
                                    "code":"NOT_FOUND_PAYMENT_SESSION",
                                    "message":"결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."
                                }
                        """)
                        .contentType(MediaType.APPLICATION_JSON));


        assertThatThrownBy(
                () -> paymentClient.confirmPayment(new TossPaymentRequestDto(
                        "wrongPaymentKey", "orderId", 1000L
                ))
        ).isInstanceOf(PaymentConfirmClientException.class);
    }

    @DisplayName("결제 요청 금액과 실제 주문 상품 가격이 다르면 400에러가 반환된다.")
    @Test
    void invalidPaymentAmountExceptionTest() {
        assertThatThrownBy(
                () -> paymentClient.confirmPayment(new TossPaymentRequestDto(
                        "paymentKey", "RESERVATIONthisisorderid", 1500L
                ))
        ).isInstanceOf(BadRequestException.class);
    }
}
