package roomescape.payment.ui;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.payment.application.PaymentService;
import roomescape.payment.application.dto.PaymentDataRequest;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    public ResponseEntity<Void> savePaymentData(@RequestBody final PaymentDataRequest request, HttpSession session) {
        session.setAttribute("paymentData", request);
        return ResponseEntity.ok().build();
    }
}
