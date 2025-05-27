package roomescape.service.payment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;

class PaymentServiceTest {

    /*
    final Payment payment = restClient.post().uri("https://api.tosspayments.com/v1/payments/confirm")
                .header("Authorization", authorizations)
                .body(requestBody)
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK,
                        (request, response) -> {
                            throw new IllegalStateException("[ERROR] 결제 승인 중 예외가 발생하였습니다.");
                        }
                ).body(Payment.class);
     */
    @Test
    @DisplayName("")
    void aa() {
        // given
        final RestClient client = Mockito.mock(RestClient.class);
        final PaymentService service = new PaymentService(client);

        Mockito.when(
                client.post().retrieve().body(Payment.class)
        ).thenReturn(new Payment("paymentKey"));

        // when
        service.approvePayment("paymentKey", "orderId", 10000);

        // then
        Mockito.verify(client.post().retrieve().body(Payment.class));
    }

}