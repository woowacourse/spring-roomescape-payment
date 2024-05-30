package roomescape.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.dto.response.PaymentResponse;

@FeignClient(name = "paymentClient", url = "https://api.tosspayments.com", configuration = PaymentClientConfig.class)
public interface PaymentClient {

    @PostMapping("/v1/payments/confirm")
    ResponseEntity<PaymentResponse> paymentReservation(@RequestHeader(name = "Authorization") String authorization,
                                                       PaymentRequest paymentRequest);
}
