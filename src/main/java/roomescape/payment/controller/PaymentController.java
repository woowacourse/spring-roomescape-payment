package roomescape.payment.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.dto.ResourcesResponse;
import roomescape.payment.controller.dto.response.PaymentResponse;
import roomescape.payment.service.PaymentService;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/payments")
    public ResponseEntity<ResourcesResponse<PaymentResponse>> findAll() {
        List<PaymentResponse> payments = paymentService.findAll();
        ResourcesResponse<PaymentResponse> response = new ResourcesResponse<>(payments);

        return ResponseEntity.ok(response);
    }
}
