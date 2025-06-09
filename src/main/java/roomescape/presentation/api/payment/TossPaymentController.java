package roomescape.presentation.api.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "토스 결제 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class TossPaymentController {

    private static final String PAYMENTS_URL = "/payments/%d";

    private final TossPaymentValidator tossPaymentValidator;

    @Operation(
            summary = "토스 결제 검증",
            description = "토스 결제 검증을 위한 API입니다. 결제 정보가 유효한지 확인합니다."
    )
    @PostMapping("/validation")
    public ResponseEntity<Void> createValidation(@Valid @RequestBody final TossPaymentValidationRequest paymentValidationRequest) {
        tossPaymentValidator.register(paymentValidationRequest.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
