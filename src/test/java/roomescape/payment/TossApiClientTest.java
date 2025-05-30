package roomescape.payment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.exception.TossPaymentClientException;
import roomescape.payment.exception.TossPaymentServerException;
import roomescape.payment.infrastructure.TossApiClient;

@RestClientTest(value = TossApiClient.class)
class TossApiClientTest {

    @Autowired
    private TossApiClient tossApiClient;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestClient restClient(RestClient.Builder builder) {
            return builder.build();
        }
    }

    @Test
    void authPayment_success() {
        PaymentResponse expected = new PaymentResponse("tgen_20250528204823hWav3", "MC4xNTU3MDQ1MDk3Njkx", "NORMAL",
                50000, "DONE", "2025-05-28T20:48:23+09:00");
        setUpForSuccess();

        PaymentResponse paymentResponse = tossApiClient.authPayment("tgen_20250528204823hWav3", "MC4xNTU3MDQ1MDk3Njkx",
                50000, "NORMAL");

        Assertions.assertThat(paymentResponse).isEqualTo(expected);
    }

    @Test
    void authPayment_shouldReturnErrorWhenServerError() {
        setUpForServerError();
        assertThatThrownBy(() -> tossApiClient.authPayment("tgen_20250528204823hWav3", "MC4xNTU3MDQ1MDk3Njkx",
                50000, "NORMAL"))
                .isInstanceOf(TossPaymentServerException.class)
                .hasMessageContaining("서버 오류입니다. 서버 관리자한테 문의해주세요.");
    }

    @Test
    void authPayment_shouldReturnErrorWhenTossServerError() {
        setUpForTossServerError();
        assertThatThrownBy(() -> tossApiClient.authPayment("tgen_20250528204823hWav3", "MC4xNTU3MDQ1MDk3Njkx",
                50000, "NORMAL"))
                .isInstanceOf(TossPaymentServerException.class)
                .hasMessageContaining("토스 서버로 문의해주세요");
    }

    @Test
    void authPayment_shouldReturnErrorWhenClientError() {
        setUpForClientError();
        assertThatThrownBy(() -> tossApiClient.authPayment("tgen_20250528204823hWav3", "MC4xNTU3MDQ1MDk3Njkx",
                50000, "NORMAL"))
                .isInstanceOf(TossPaymentClientException.class)
                .hasMessageContaining("카드 사용이 거절되었습니다. 카드사 문의가 필요합니다.");
    }

    private void setUpForSuccess() {
        String expectedNormalResponse = """
                    {
                      "mId": "tgen_docs",
                      "lastTransactionKey": "txrd_a01jwbbjdnsn9xbgy96k7m7ce57",
                      "paymentKey": "tgen_20250528204823hWav3",
                      "orderId": "MC4xNTU3MDQ1MDk3Njkx",
                      "orderName": "토스 티셔츠 외 2건",
                      "taxExemptionAmount": 0,
                      "status": "DONE",
                      "requestedAt": "2025-05-28T20:48:23+09:00",
                      "approvedAt": "2025-05-28T20:48:46+09:00",
                      "useEscrow": false,
                      "cultureExpense": false,
                      "card": null,
                      "virtualAccount": null,
                      "transfer": null,
                      "mobilePhone": null,
                      "giftCertificate": null,
                      "cashReceipt": null,
                      "cashReceipts": null,
                      "discount": null,
                      "cancels": null,
                      "secret": "ps_AQ92ymxN341NYWKMRym4VajRKXvd",
                      "type": "NORMAL",
                      "easyPay": {
                        "provider": "토스페이",
                        "amount": 50000,
                        "discountAmount": 0
                      },
                      "country": "KR",
                      "failure": null,
                      "isPartialCancelable": true,
                      "receipt": {
                        "url": "https://dashboard.tosspayments.com/receipt/redirection?transactionId=tgen_20250528204823hWav3&ref=PX"
                      },
                      "checkout": {
                        "url": "https://api.tosspayments.com/v1/payments/tgen_20250528204823hWav3/checkout"
                      },
                      "currency": "KRW",
                      "totalAmount": 50000,
                      "balanceAmount": 50000,
                      "suppliedAmount": 45455,
                      "vat": 4545,
                      "taxFreeAmount": 0,
                      "method": "간편결제",
                      "version": "2022-11-16",
                      "metadata": null
                    }
                """;
        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(expectedNormalResponse, MediaType.APPLICATION_JSON));
    }

    private void setUpForServerError() {
        String expectedErrorResponse = """
                {
                  "code": "INVALID_API_KEY",
                  "message": "잘못된 시크릿키 연동 정보 입니다."
                }
                
                """;
        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedErrorResponse));
    }

    private void setUpForClientError() {
        String expectedErrorResponse = """
                {
                  "code": "INVALID_REJECT_CARD",
                  "message": "카드 사용이 거절되었습니다. 카드사 문의가 필요합니다."
                }
                
                """;
        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedErrorResponse));
    }

    private void setUpForTossServerError() {
        String expectedErrorResponse = """
                {
                  "code": "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
                  "message": "결제가 완료되지 않았어요. 다시 시도해주세요."
                }
                
                """;
        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedErrorResponse));
    }
}
