package roomescape.payment.ui;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.response.ApiResponse;
import roomescape.common.session.SessionManager;
import roomescape.payment.application.dto.PrePaymentRequest;

@RestController
@AllArgsConstructor
public class PaymentController {
    private final SessionManager sessionManager;

    @PostMapping("/prepay")
    public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody PrePaymentRequest request,
                                                    HttpSession session) {
        sessionManager.saveToSession(session, request.orderId(), request.amount());
        ApiResponse<Void> apiResponse = ApiResponse.createSuccessWithNoData();
        return ResponseEntity.ok(apiResponse);
    }
}
