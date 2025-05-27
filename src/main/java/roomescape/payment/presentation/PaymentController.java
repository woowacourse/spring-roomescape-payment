package roomescape.payment.presentation;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.security.annotation.RequireRole;
import roomescape.member.domain.MemberRole;
import roomescape.payment.application.PaymentService;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.request.PaymentRequest;

import roomescape.payment.presentation.dto.response.PaymentApproveResponse;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RequireRole(MemberRole.REGULAR)
    @PostMapping("/approve")
    public ResponseEntity<PaymentApproveResponse> approvePayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentApproveResponse paymentApproveResponse = paymentService.approvePayment(PaymentApproveRequest.from(paymentRequest));
        return ResponseEntity.ok().body(paymentApproveResponse);
    }
}
