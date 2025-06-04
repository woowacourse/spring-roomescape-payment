package roomescape.presentation.api.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.payment.PaymentValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private static final String PAYMENTS_URL = "/payments/%d";

    private final PaymentValidator paymentValidator;

    @PostMapping("/validation")
    public ResponseEntity<Void> createValidation(@Valid @RequestBody final PaymentValidationRequest paymentValidationRequest) {
        paymentValidator.register(paymentValidationRequest.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
