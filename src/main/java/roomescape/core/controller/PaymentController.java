package roomescape.core.controller;

import jakarta.validation.Valid;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentConfirmResponse> create(@Valid @RequestBody final PaymentRequest request,
                                                         final LoginMember loginMember) {
        final PaymentConfirmResponse response = paymentService.confirmPayment(request, loginMember);
        logger.info("Reservation {} payment {} confirmed.", request.getReservationId(), request.getPaymentKey());
        logger.info("Total amount: {}, Order Id: {}", response.getTotalAmount(), response.getOrderId());

        return ResponseEntity.created(URI.create("/payments/" + response.getId()))
                .body(response);
    }
}
