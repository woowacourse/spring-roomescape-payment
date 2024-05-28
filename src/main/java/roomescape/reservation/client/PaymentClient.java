package roomescape.reservation.client;

import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.dto.response.PaymentResponse;

@FeignClient(name = "paymentClient", url = "https://api.tosspayments.com/v1/payments/confirm")
public interface PaymentClient {


    @PostMapping
    ResponseEntity<PaymentResponse> paymentReservation(@RequestHeader(name = "Authorization") String authorization, PaymentRequest paymentRequest);
}
