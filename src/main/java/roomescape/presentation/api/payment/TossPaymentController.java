package roomescape.presentation.api.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.payment.toss.TossPaymentValidator;
import roomescape.presentation.api.payment.dto.TossPaymentValidationRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class TossPaymentController {

    private static final String PAYMENTS_URL = "/payments/%d";

    private final TossPaymentValidator tossPaymentValidator;

    @PostMapping("/validation")
    public ResponseEntity<Void> createValidation(@Valid @RequestBody final TossPaymentValidationRequest paymentValidationRequest) {
        tossPaymentValidator.register(paymentValidationRequest.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
