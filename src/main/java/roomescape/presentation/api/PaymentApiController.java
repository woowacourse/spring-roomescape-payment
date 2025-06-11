package roomescape.presentation.api;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AuthRequired;
import roomescape.auth.LoginInfo;
import roomescape.business.service.PaymentApproveService;
import roomescape.business.service.PaymentService;
import roomescape.presentation.dto.request.PaymentAndReservationRequest;
import roomescape.presentation.dto.response.PaymentResponse;

@RestController
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentApproveService paymentApproveService;
    private final PaymentService paymentService;

    @PostMapping("/payments")
    @AuthRequired
    public ResponseEntity<PaymentResponse> createPayment(LoginInfo loginInfo,
                                                         @RequestBody PaymentAndReservationRequest request) {
        PaymentResponse response = paymentService.createPaymentAndReservation(loginInfo, request);
        return ResponseEntity.created(URI.create("/payments/" + response.id())).body(response);
    }

    @PatchMapping("/payments/{paymentId}")
    @AuthRequired
    public ResponseEntity<Void> approvePayment(@PathVariable("paymentId") String paymentId,
                                               @RequestBody @Valid PaymentApproveRequest request) {
        paymentApproveService.requestApproveAndCompletePayment(paymentId, request);
        return ResponseEntity.noContent().build();
    }
}
