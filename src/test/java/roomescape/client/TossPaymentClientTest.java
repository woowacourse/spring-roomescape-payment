package roomescape.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static roomescape.fixture.MockServerTestFixture.BASE_URL;
import static roomescape.fixture.MockServerTestFixture.BUILDER;
import static roomescape.fixture.MockServerTestFixture.SERVER;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import roomescape.client.dto.request.TossPaymentConfirmRequest;
import roomescape.client.dto.response.TossPaymentResponse;
import roomescape.common.exception.PaymentException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TossPaymentClientTest {

    private final TossPaymentClient tossPaymentClient = new TossPaymentClient(BUILDER.build());

    @BeforeEach
    void setUp() {
        SERVER.reset();
    }

    @Test
    void 결제_요청을_성공한다() {
        // given
        TossPaymentResponse expected = new TossPaymentResponse(
                "tgen_20250528204823hWav3",
                "MC4xNTU3MDQ1MDk3Njkx",
                "토스 티셔츠 외 2건",
                "DONE",
                null,
                "간편결제",
                1000L,
                null
        );

        TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );
        setUpSuccess();
        // when
        TossPaymentResponse actual = tossPaymentClient.confirmPayment(request);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 서버_에러로_인해_결제_요청을_실패한다() {
        // given
        TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );
        setUpServerError();

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirmPayment(request))
                .isInstanceOf(PaymentException.class);
    }

    @Test
    void 클라이언트_에러로_인해_결제_요청을_실패한다() {
        // given
        TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );
        setUpClientError();

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirmPayment(request))
                .isInstanceOf(PaymentException.class);
    }

    private void setUpSuccess() {
        String expectedResponse = """
                {
                  "paymentKey": "tgen_20250528204823hWav3",
                  "orderId": "MC4xNTU3MDQ1MDk3Njkx",
                  "orderName": "토스 티셔츠 외 2건",
                  "status": "DONE",
                  "requestedAt": null,
                  "method": "간편결제",
                  "totalAmount": 1000,
                  "card": null
                } 
                """;

        SERVER.expect(requestTo(BASE_URL + "/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));
    }

    private void setUpServerError() {
        String expectedError = """
                {
                "code": "FAILED",
                "message":"서버 에러로 결제 실패."
                }
                """;
        SERVER.expect(requestTo(BASE_URL + "/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedError));
    }

    private void setUpClientError() {
        String expectedError = """
                {
                "code": "FAILED",
                "message":"클라이언트 에러로 결제 실패."
                }
                """;
        SERVER.expect(requestTo(BASE_URL + "/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedError));
    }
}
