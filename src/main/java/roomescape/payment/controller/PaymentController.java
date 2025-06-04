package roomescape.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.auth.LoginMember;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.payment.controller.dto.PaymentVerificationWebRequest;
import roomescape.payment.controller.dto.PaymentVerificationWebResponse;
import roomescape.payment.service.PaymentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<PaymentVerificationWebResponse> createPaymentVerification(
            @RequestBody final PaymentVerificationWebRequest paymentVerificationWebRequest,
            @LoginMember final MemberInfo memberInfo
    ) {
        final PaymentVerificationWebResponse response = paymentService.createPrepayment(
                paymentVerificationWebRequest,
                memberInfo.id()
        );

        return ResponseEntity.ok(response);
    }
}
