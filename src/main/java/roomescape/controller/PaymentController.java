package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.annotation.Auth;
import roomescape.dto.PaymentApproveRequest;
import roomescape.service.PaymentService;

@Controller
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> approve(@Auth long memberId, @RequestBody PaymentApproveRequest paymentApproveRequest) {
        paymentService.approve(paymentApproveRequest, memberId);

        return ResponseEntity.ok().build();
    }
}
