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
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final TossRestClient tossRestClient;
    private final PaymentService paymentService;

    @PostMapping("/confirm/tossPay")
    public TossPaymentResponse confirmPayment(@RequestBody @Valid ReservationPaymentRequest request) {
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );
        return tossRestClient.confirm(tossPaymentRequest);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody @Valid ReservationPaymentRequest request, LoginMember loginMember) {
        paymentService.save(request, loginMember);
    }
}
