package roomescape.controller;

import static roomescape.exception.ExceptionType.NOT_FOUND_RESERVATION;
import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_BALANCE;
import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_CARD_COMPANY;
import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_EXCEED_MAX_ONE_DAY_AMOUNT;
import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_HIDDEN;
import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_INVALID_PASSWORD;
import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_INVALID_STOPPED_CARD;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.Auth;
import roomescape.annotation.ErrorApiResponse;
import roomescape.dto.PaymentApproveRequest;
import roomescape.service.PaymentService;

@RestController
@Tag(name = "결제", description = "결제 관련 API")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    @Operation(summary = "결제 승인", description = "결제를 승인할 때 사용하는 API. 예약 생성 API 호출 후 그 응답을 이용해 호출해야 한다.")
    @ErrorApiResponse(value = {NOT_FOUND_RESERVATION, PAYMENT_FAIL_CAUSE_BALANCE, PAYMENT_FAIL_CAUSE_CARD_COMPANY,
            PAYMENT_FAIL_CAUSE_INVALID_PASSWORD, PAYMENT_FAIL_CAUSE_EXCEED_MAX_ONE_DAY_AMOUNT,
            PAYMENT_FAIL_CAUSE_INVALID_STOPPED_CARD, PAYMENT_FAIL_CAUSE_HIDDEN})
    public ResponseEntity<Void> approve(@Auth long memberId, @RequestBody PaymentApproveRequest paymentApproveRequest) {
        paymentService.approve(paymentApproveRequest, memberId);

        return ResponseEntity.ok().build();
    }
}
