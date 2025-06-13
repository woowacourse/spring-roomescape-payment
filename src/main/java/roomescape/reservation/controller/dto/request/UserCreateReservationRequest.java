package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.payment.model.TossPaymentApprovalInfo;
import roomescape.reservation.application.dto.request.CreateReservationServiceRequest;

public record UserCreateReservationRequest(
        @NotNull(message = "날짜를 필수로 입력해야 합니다.")
        LocalDate date,
        @NotNull(message = "시간을 필수로 입력해야 합니다.")
        Long timeId,
        @NotNull(message = "테마를 필수로 입력해야 합니다.")
        Long themeId,
        @NotBlank(message = "결제 키를 필수로 입력해야 합니다.")
        String paymentKey,
        @NotBlank(message = "주문 ID를 필수로 입력해야 합니다.")
        String orderId,
        @NotNull(message = "금액을 필수로 입력해야 합니다.")
        int amount
) {

    public TossPaymentApprovalInfo toTossPaymentApproveInfo() {
        return TossPaymentApprovalInfo.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();
    }

    public CreateReservationServiceRequest toServiceRequest(Long memberId) {
        return CreateReservationServiceRequest.builder()
                .date(date)
                .timeId(timeId)
                .memberId(memberId)
                .themeId(themeId)
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();
    }
}
