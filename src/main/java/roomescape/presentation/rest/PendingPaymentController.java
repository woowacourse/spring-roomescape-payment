package roomescape.presentation.rest;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.PendingPaymentService;
import roomescape.application.request.PaymentInfo;
import roomescape.presentation.response.ReservedResponse;

@RestController
public class PendingPaymentController {

    private final PendingPaymentService pendingPaymentService;

    public PendingPaymentController(final PendingPaymentService pendingPaymentService) {
        this.pendingPaymentService = pendingPaymentService;
    }

    @PatchMapping("/pending-payments/{reservationId}/payment")
    public ReservedResponse confirmPayment(@PathVariable(name = "reservationId") final long reservationId,
                                           @RequestBody PaymentInfo paymentInfo) {
        return pendingPaymentService.completePayment(reservationId, paymentInfo);
    }
}
