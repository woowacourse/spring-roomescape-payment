package roomescape.client;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.http.ResponseEntity;
import roomescape.client.dto.request.TossPaymentConfirmRequest;
import roomescape.client.dto.response.TossErrorResponse;
import roomescape.client.dto.response.TossPaymentResponse;

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
                null,
                null
        );

        TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );
        setUpSuccess();
        // when
        ResponseEntity<TossPaymentResponse> actual = tossPaymentClient.confirmPayment(request);

        // then
        assertThat(actual.getBody()).isEqualTo(expected);
    }

    @Test
    void 서버_에러로_인해_결제_요청을_실패한다() {
        // given
        TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );

        TossErrorResponse expected = new TossErrorResponse(
                "INVALID_API_KEY",
                "잘못된 시크릿키 연동 정보 입니다."
        );
        setUpServerError();

        // when
        ResponseEntity<TossPaymentResponse> actual = tossPaymentClient.confirmPayment(request);

        // then
        assertThat(actual.getBody().failure()).isNotNull();
        assertThat(actual.getBody().failure()).isEqualTo(expected);
    }

    @Test
    void 클라이언트_에러로_인해_결제_요청을_실패한다() {
        // given
        TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );
        TossErrorResponse expected = new TossErrorResponse(
                "INVALID_CARD",
                "유효하지 않은 카드입니다."
        );
        setUpClientError();

        // when
        ResponseEntity<TossPaymentResponse> actual = tossPaymentClient.confirmPayment(request);

        // then
        assertThat(actual.getBody().failure()).isNotNull();
        assertThat(actual.getBody().failure()).isEqualTo(expected);
    }

    private void setUpSuccess() {
        String expectedResponse = """
                {
                  "paymentKey": "tgen_20250528204823hWav3",
                  "orderId": "MC4xNTU3MDQ1MDk3Njkx",
                  "orderName": "토스 티셔츠 외 2건",
                  "status": "DONE",
                  "method": "간편결제",
                  "totalAmount": 1000,
                  "card": null,
                  "failure": null
                } 
                """;

        SERVER.expect(requestTo(BASE_URL + "/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));
    }

    private void setUpServerError() {
        String expectedError = """
                {
                  "paymentKey": "tgen_20250528204823hWav3",
                  "orderId": "MC4xNTU3MDQ1MDk3Njkx",
                  "orderName": "토스 티셔츠 외 2건",
                  "status": "ABORTED",
                  "method": "간편결제",
                  "totalAmount": 1000,
                  "card": null,
                  "failure": {
                    "code": "INVALID_API_KEY",
                    "message": "잘못된 시크릿키 연동 정보 입니다."
                    } 
                }
                """;
        SERVER.expect(requestTo(BASE_URL + "/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedError)
                        .header("status", "500"));
    }

    private void setUpClientError() {
        String expectedError = """
                {
                  "paymentKey": "tgen_20250528204823hWav3",
                  "orderId": "MC4xNTU3MDQ1MDk3Njkx",
                  "orderName": "토스 티셔츠 외 2건",
                  "status": "ABORTED",
                  "method": "간편결제",
                  "totalAmount": 1000,
                  "card": null,
                  "failure": {
                    "code": "INVALID_CARD",
                    "message": "유효하지 않은 카드입니다."
                    } 
                }
                """;
        SERVER.expect(requestTo(BASE_URL + "/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedError));
    }
}
