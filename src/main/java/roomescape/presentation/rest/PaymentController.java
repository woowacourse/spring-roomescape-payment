package roomescape.presentation.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.PaymentService;
import roomescape.presentation.request.ReservationPaymentRequest;

@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.OK)
    public void confirmReservation(@RequestBody ReservationPaymentRequest request) {
        service.confirm(request.reservationId(), request.paymentKey(), request.orderId(), request.amount());
    }
}
