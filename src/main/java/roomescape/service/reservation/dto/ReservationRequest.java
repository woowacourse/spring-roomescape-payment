package roomescape.service.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.service.payment.dto.PaymentConfirmInput;

import java.time.LocalDate;

public class ReservationRequest {
    @NotNull(message = "date 값이 null일 수 없습니다.")
    private final LocalDate date;
    @NotNull(message = "timeId 값이 null일 수 없습니다.")
    private final Long timeId;
    @NotNull(message = "themeId 값이 null일 수 없습니다.")
    private final Long themeId;
    @NotBlank(message = "paymentKey 값이 null 또는 공백일 수 없습니다.")
    private final String paymentKey;
    @NotBlank(message = "orderId 값이 null 또는 공백일 수 없습니다.")
    private final String orderId;
    @NotNull(message = "amount 값이 null일 수 없습니다.")
    private final Integer amount;

    public ReservationRequest(
            String date, String timeId, String themeId, String paymentKey, String orderId, Integer amount) {
        this.date = LocalDate.parse(date);
        this.timeId = Long.parseLong(timeId);
        this.themeId = Long.parseLong(themeId);
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public ReservationSaveInput toReservationSaveInput() {
        return new ReservationSaveInput(date, timeId, themeId);
    }

    public PaymentConfirmInput toPaymentConfirmInput() {
        return new PaymentConfirmInput(orderId, amount, paymentKey);
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getTimeId() {
        return timeId;
    }

    public Long getThemeId() {
        return themeId;
    }
}
