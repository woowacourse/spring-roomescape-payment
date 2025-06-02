package roomescape.reservation.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

public class TossPaymentClientTest {

    @Test
    void tossApiInvalidCardExpirationTest() {
        TossApiInvalidCardExpirationTest client = new TossApiInvalidCardExpirationTest();
        Assertions.assertThatThrownBy(client::testInvalidCardExpiration)
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining("카드 정보를 다시 확인해주세요");
    }

    private static class TossApiInvalidCardExpirationTest {
        private final RestClient restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("TossPayments-Test-Code", "INVALID_CARD_EXPIRATION")
                .build();

        public void testInvalidCardExpiration() {
            String jsonBody = """
            {
              "cardNumber": "1234567890123456",
              "cardExpirationYear": "99",
              "cardExpirationMonth": "12",
              "cardPassword": "11",
              "customerIdentityNumber": "881212",
              "amount": 1000,
              "orderId": "order-id-1234",
              "orderName": "테스트 결제"
            }
            """;

            try {
                restClient.post()
                        .uri("/v1/payments/key-in")
                        .body(jsonBody)
                        .retrieve()
                        .toEntity(String.class); // 성공 시 ResponseEntity<String> 반환

            } catch (HttpClientErrorException e) {
                System.err.println("HTTP 오류 상태: " + e.getStatusCode());
                System.err.println("오류 메시지: " + e.getResponseBodyAsString());
                throw e; // 테스트에서 예외를 검증할 수 있도록 다시 throw
            } catch (RestClientException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
}
