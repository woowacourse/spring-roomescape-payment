package roomescape.presentation.api.payment;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.payment.CreatePaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final String PAYMENTS_URL = "/payments/%d";

    private final CreatePaymentService createPaymentService;

    public PaymentController(CreatePaymentService createPaymentService) {
        this.createPaymentService = createPaymentService;
    }

    @PostMapping
    public ResponseEntity<Void> createPayment(@Valid @RequestBody CreatePaymentRequest createPaymentRequest) {
        Long id = createPaymentService.register(createPaymentRequest.toPaymentCommand());
        return ResponseEntity.created(URI.create(PAYMENTS_URL.formatted(id))).build();
    }
}
