package roomescape.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.service.TossPaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final TossPaymentService tossPaymentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/confirm/tossPay")
    public void createReservationAndConfirmPayment(@RequestBody @Valid final ReservationPaymentRequest request, final LoginMember loginMember) {
        tossPaymentService.registerAndPayForReservation(request, loginMember);
        tossPaymentService.confirmPayment(request);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/confirm/tossPay/{reservationId}")
    public void completePaymentForReservation(
            @PathVariable(value = "reservationId") Long reservationId,
            @RequestBody @Valid final PaymentRequest request,
            final LoginMember loginMember
    ) {
        tossPaymentService.completePaymentForReservation(reservationId, request, loginMember);
    }
}
