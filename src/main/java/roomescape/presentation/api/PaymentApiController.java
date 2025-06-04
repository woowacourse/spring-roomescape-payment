package roomescape.presentation.api;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.business.service.PaymentService;
import roomescape.presentation.dto.request.PaymentRequest;

@RestController
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentService paymentService;

    @PostMapping("/payments")
    public ResponseEntity<Void> createPayment(@RequestBody PaymentRequest request) {
        String paymentId = paymentService.createPayment(request);
        return ResponseEntity.created(URI.create("/payments/" + paymentId)).build();
    }
}
