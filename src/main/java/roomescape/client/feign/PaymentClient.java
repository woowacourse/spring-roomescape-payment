package roomescape.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.dto.response.PaymentResponse;

@FeignClient(name = "paymentClient", url = "https://api.tosspayments.com/v1/payments/confirm", configuration = PaymentFeignConfig.class)
public interface PaymentClient {

    @PostMapping
    ResponseEntity<PaymentResponse> paymentReservation(
            @RequestHeader(name = "Authorization") String authorization,
            PaymentRequest paymentRequest);
}
