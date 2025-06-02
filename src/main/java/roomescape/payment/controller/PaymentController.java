package roomescape.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.service.TossPaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final TossPaymentService tossPaymentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/confirm/tossPay")
    public void confirmPayment(@RequestBody @Valid final ReservationPaymentRequest request, final LoginMember loginMember) {
        tossPaymentService.saveReservationPayment(request, loginMember);
        tossPaymentService.confirmPayment(request);
    }
}
