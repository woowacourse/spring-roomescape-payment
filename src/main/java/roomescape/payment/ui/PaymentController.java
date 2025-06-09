package roomescape.payment.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.payment.application.dto.PrePaymentValidRequest;

@Tag(name = "결제", description = "결제 관련 API")
@RestController
public class PaymentController {

    public static final String PRE_PAYMENT = "prePayment";

    @Operation(summary = "선결제 정보 저장 API", description = "선결제 정보를 세션에 저장합니다. 결제 요청 전에 호출되어야 합니다.")
    @PostMapping("/payments")
    public ResponseEntity<Void> setPrePayment(@RequestBody final PrePaymentValidRequest request, HttpSession session) {
        session.setAttribute(PRE_PAYMENT, request);
        return ResponseEntity.ok().build();
    }
}
