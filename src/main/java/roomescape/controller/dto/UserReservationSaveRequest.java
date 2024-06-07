package roomescape.controller.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import roomescape.service.dto.request.ReservationSaveRequest;

import java.time.LocalDate;

public record UserReservationSaveRequest(
        @NotNull(message = "날짜를 입력해주세요")
        @FutureOrPresent(message = "지나간 날짜의 예약을 할 수 없습니다.")
        LocalDate date,

        @NotNull(message = "시간을 선택해주세요")
        @Positive(message = "잘못된 시간입력이 들어왔습니다.")
        Long timeId,

        @NotNull(message = "테마를 선택해주세요")
        @Positive(message = "잘못된 테마입력이 들어왔습니다")
        Long themeId,

        @NotNull(message = "paymentKey가 발급되지 않았습니다.")
        String paymentKey,

        @NotNull(message = "결제 id 가 비어있습니다")
        String orderId,

        @NotNull(message = "결제 금액이 정상입력되지 않았습니다.")
        String amount,

        @NotNull(message = "결제 타입이 비어있습니다.")
        String paymentType
) {
    public ReservationSaveRequest toReservationSaveRequest(Long memberId) {
        return new ReservationSaveRequest(memberId, date, timeId, themeId);
    }
}