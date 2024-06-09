package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.service.PaymentService;

@RestController
@RequestMapping("/payments")
@Tag(name = "결제 API", description = "결제 관련 API 입니다.")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Operation(summary = "예약 결제", description = "예약을 결제합니다.")
    public ResponseEntity<PaymentResponse> payReservation(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.payReservation(paymentRequest);

        return ResponseEntity.created(URI.create("/payments/" + response.id()))
                .body(response);
    }
}
