package roomescape.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import roomescape.service.dto.ReservationPaymentRequest;

public record UserReservationSaveRequest(
        @NotNull
        @FutureOrPresent(message = "지나간 날짜의 예약을 할 수 없습니다.")
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,

        @NotNull
        @Positive
        Long timeId,

        @NotNull
        @Positive
        Long themeId,

        Integer amount,

        String orderId,

        String paymentKey
) {

    public ReservationPaymentRequest toReservationSaveRequest(Long memberId) {
        return new ReservationPaymentRequest(memberId, date, timeId, themeId, amount, orderId, paymentKey);
    }
}
