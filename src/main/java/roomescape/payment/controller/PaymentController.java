package roomescape.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.service.PaymentService;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/confirm/toss")
    public ResponseEntity<Void> sendConfirmToToss(@RequestBody PaymentConfirmRequest paymentRequest) {
        paymentService.sendPaymentConfirmToToss(paymentRequest);

        return ResponseEntity.ok().build();
    }
}
