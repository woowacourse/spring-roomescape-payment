package roomescape.payment.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record PaymentCancelRequest(
        @NotBlank @Length(max = 20) String cancelReason
) {
    public static PaymentCancelRequest makePaymentCancelRequest() {
        return new PaymentCancelRequest("예약 취소로 인한 결제 취소");
    }
}
