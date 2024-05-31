package roomescape.domain.payment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.payment.dto.SavePaymentCredentialRequest;
import roomescape.domain.payment.service.PaymentService;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments/credentials")
    public void saveCredentials(@RequestBody SavePaymentCredentialRequest request) {
        paymentService.saveCredential(request);
    }
}
