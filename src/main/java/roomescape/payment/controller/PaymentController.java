package roomescape.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Payment", description = "결제 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "토스페이먼츠 결제 승인", description = "토스페이먼츠 결제를 승인합니다.")
    @PostMapping("{id}/confirm/tossPay")
    public TossPaymentResponse confirmPayment(
            @Parameter(description = "결제 ID") @NotNull @PathVariable final Long id,
            @Parameter(description = "결제 승인 요청 정보") @RequestBody @Valid final ReservationPaymentRequest request
    ) {
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );
        return paymentService.confirm(tossPaymentRequest, id);
    }

    @Operation(summary = "결제 생성", description = "새로운 결제를 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse savePayment(
            @Parameter(description = "결제 생성 요청 정보") @RequestBody @Valid final ReservationPaymentRequest request,
            @Parameter(description = "로그인한 회원 정보") final LoginMember loginMember
    ) {
        return paymentService.savePayment(request, loginMember);
    }
}
