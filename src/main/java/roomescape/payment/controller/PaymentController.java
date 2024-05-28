package roomescape.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.service.PaymentClient;

@RestController
public class PaymentController {

    private final PaymentClient paymentClient;

    public PaymentController(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @PostMapping("/confirm/toss")
    public ResponseEntity<Void> sendConfirmToToss(@RequestBody PaymentRequest paymentRequest) {
        paymentClient.addPayment(paymentRequest);

        return ResponseEntity.ok().build();
    }
}
