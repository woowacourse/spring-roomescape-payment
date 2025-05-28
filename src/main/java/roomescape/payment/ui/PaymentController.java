package roomescape.payment.ui;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.response.ApiResponse;
import roomescape.payment.application.PaymentService;
import roomescape.payment.application.dto.PrePaymentRequest;

@RestController
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    //TODO: 엔드포인트 고민고민  (2025-05-28, 수, 12:9)
    @PostMapping("/pre-payments")
    public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody PrePaymentRequest request) {
        paymentService.savePrePayment(request);
        ApiResponse<Void> apiResponse = ApiResponse.createSuccessWithNoData();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
