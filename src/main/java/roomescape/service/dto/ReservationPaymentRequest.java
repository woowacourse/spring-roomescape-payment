package roomescape.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record ReservationPaymentRequest(
        @NotNull
        @Positive
        Long memberId,

        @NotNull
        @FutureOrPresent(message = "지나간 날짜의 예약을 할 수 없습니다.")
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
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
    public ReservationPaymentRequest(Long memberId, LocalDate date, Long timeId, Long themeId) {
        this(memberId, date, timeId, themeId, null, null, null);
    }

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(amount, orderId, paymentKey);
    }

    public ReservationRequest toReservationRequest() {
        return new ReservationRequest(memberId, date, timeId, themeId);
    }
}

