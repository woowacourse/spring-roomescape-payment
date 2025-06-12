package roomescape.approval.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.approval.application.dto.PrePaymentRequest;
import roomescape.common.response.ApiResponse;
import roomescape.common.session.SessionManager;

@Tag(name = "결제 API", description = "결제 관련 API입니다.")
@RestController
@RequestMapping("payments")
@AllArgsConstructor
public class PaymentController {
    private final SessionManager sessionManager;

    @Operation(summary = "사전 결제", description = "실제 결제 금액과 설정된 금액을 비교하기 위해 요구되는 사전결제 API입니다.")
    @PostMapping("/prepay")
    public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody PrePaymentRequest request,
                                                    HttpSession session) {
        sessionManager.saveToSession(session, request.orderId(), request.amount());
        ApiResponse<Void> apiResponse = ApiResponse.createSuccessWithNoData();
        return ResponseEntity.ok(apiResponse);
    }
}
