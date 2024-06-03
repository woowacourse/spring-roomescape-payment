package roomescape.web.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import roomescape.service.request.PaymentApproveDto;
import roomescape.service.request.ReservationSaveDto;

public record MemberReservationRequest(
        @NotBlank(message = "예약 날짜는 필수입니다.") String date,
        @NotNull @Positive Long timeId,
        @NotNull @Positive Long themeId,
        @NotBlank String paymentKey,
        @NotBlank String orderId,
        @NotNull Long amount) {

    public PaymentApproveDto toPaymentApproveDto() {
        return new PaymentApproveDto(paymentKey, orderId, amount);
    }

    public ReservationSaveDto toReservationSaveDto(Long memberId) {
        return new ReservationSaveDto(date, timeId, themeId, memberId);
    }
}
