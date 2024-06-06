package roomescape.web.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.PaymentService;
import roomescape.service.request.PaymentSaveDto;
import roomescape.service.response.PaymentDto;
import roomescape.web.auth.Auth;
import roomescape.web.controller.request.LoginMember;
import roomescape.web.controller.request.PaymentRequest;
import roomescape.web.controller.response.PaymentResponse;

import java.net.URI;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @Auth LoginMember loginMember,
                                                  @Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentSaveDto paymentSaveDto = paymentRequest.toPaymentSaveDto(loginMember.id());
        PaymentDto paymentDto = paymentService.save(paymentSaveDto);
        PaymentResponse paymentResponse = new PaymentResponse(paymentDto);

        return ResponseEntity.created(URI.create("/payments/" + paymentDto.id()))
                .body(paymentResponse);
    }
}
