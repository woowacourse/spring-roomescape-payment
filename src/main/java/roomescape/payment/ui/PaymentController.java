package roomescape.payment.ui;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginMemberId;
import roomescape.common.response.ApiResponse;
import roomescape.common.session.SessionManager;
import roomescape.payment.application.dto.PrePaymentRequest;

@RestController
@AllArgsConstructor
@Slf4j
public class PaymentController {
    private final SessionManager sessionManager;

    @Operation(
            summary = "결제 준비",
            description = "주문 ID와 결제 금액을 세션에 저장하여 결제 프로세스를 준비합니다. 이 API는 실제 결제 전에 호출되어야 합니다."
    )
    @PostMapping("/prepay")
    public ResponseEntity<ApiResponse<Void>> create(
            @Valid @RequestBody PrePaymentRequest request,
            @LoginMemberId Long memberId,
            HttpSession session
    ) {
        log.info("결제 준비 요청: memberId={}, orderId={}, amount={}", memberId, request.orderId(), request.amount());
        sessionManager.saveToSession(session, request.orderId(), request.amount());
        ApiResponse<Void> apiResponse = ApiResponse.createSuccessWithNoData();
        return ResponseEntity.ok(apiResponse);
    }
}
