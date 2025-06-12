package roomescape.payment.ui;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.payment.application.dto.PrePaymentValidRequest;

@Profile("test")
@RestController
public class PaymentTestController {

    public static final String PRE_PAYMENT = "prePayment";

    @Operation(summary = "테스트용 선결제 정보 저장 API", description = "테스트를 위해 선결제 정보를 세션에 저장합니다.")
    @PostMapping("/test/pre-payment")
    public ResponseEntity<Void> storePrePaymentInSession(
            @RequestBody @Valid final PrePaymentValidRequest request,
            final HttpSession session
    ) {
        session.setAttribute(PRE_PAYMENT, request);
        return ResponseEntity.ok().build();
    }
}
