package roomescape.payment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.service.PaymentService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("{id}/confirm/tossPay")
    public TossPaymentResponse confirmPayment(
            @NotNull @PathVariable final Long id,
            @RequestBody @Valid final ReservationPaymentRequest request
    ) {
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );
        return paymentService.confirm(tossPaymentRequest, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse savePayment(@RequestBody @Valid final ReservationPaymentRequest request, final LoginMember loginMember) {
        return paymentService.savePayment(request, loginMember);
    }
}
