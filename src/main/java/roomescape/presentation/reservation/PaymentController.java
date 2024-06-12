package roomescape.presentation.reservation;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.payment.PaymentService;
import roomescape.application.payment.dto.PaymentRequest;
import roomescape.application.payment.dto.PaymentResponse;
import roomescape.domain.payment.Payment;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentResponse purchase(@Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.purchase(request);
        return new PaymentResponse(
                payment.getOrderId(), payment.getOrderId(), payment.getAmount(), payment.getStatus()
        );
    }
}
