package roomescape.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // TODO: 이름
    @PostMapping
    public ResponseEntity<PaymentResponse> savePaidReservation(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.askPayment(paymentRequest);

        return ResponseEntity.created(URI.create("/payments/" + response.id()))
                .body(response);
    }
}
