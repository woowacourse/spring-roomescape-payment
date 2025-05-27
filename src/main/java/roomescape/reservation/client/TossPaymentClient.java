package roomescape.reservation.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import roomescape.reservation.service.PaymentApprovalRequest;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final RestClient restClient;

    public ResponseEntity<Void> approvePayment(PaymentApprovalRequest request) {
        return restClient.post().uri("https://api.tosspayments.com/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                // TODO: 인증 토큰 application.yml 분리
                .header("Authorization", "Basic dGVzdF9nc2tfZG9jc19PYVB6OEw1S2RtUVhrelJ6M3k0N0JNdzY6")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
