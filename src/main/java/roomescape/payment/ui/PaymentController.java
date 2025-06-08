package roomescape.payment.ui;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.payment.application.dto.PrePaymentValidRequest;

@RestController
public class PaymentController {

    public static final String PRE_PAYMENT = "prePayment";

    @PostMapping("/payments")
    public ResponseEntity<Void> setPrePayment(@RequestBody final PrePaymentValidRequest request, HttpSession session) {
        session.setAttribute(PRE_PAYMENT, request);
        return ResponseEntity.ok().build();
    }
}
