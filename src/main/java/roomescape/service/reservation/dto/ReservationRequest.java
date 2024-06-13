package roomescape.service.reservation.dto;

import java.time.DateTimeException;
import java.time.LocalDate;
import roomescape.exception.common.InvalidRequestBodyException;
import roomescape.service.payment.dto.PaymentConfirmInput;

public class ReservationRequest {
    private final LocalDate date;
    private final Long timeId;
    private final Long themeId;
    private final String paymentKey;
    private final String orderId;
    private final Long amount;

    public ReservationRequest(
            String date, String timeId, String themeId, String paymentKey, String orderId, Long amount) {
        validate(date, timeId, themeId, paymentKey, orderId, amount);
        this.date = LocalDate.parse(date);
        this.timeId = Long.parseLong(timeId);
        this.themeId = Long.parseLong(themeId);
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public void validate(
            String date, String timeId, String themeId, String paymentKey, String orderId, Long amount) {
        if (date == null || date.isBlank() ||
                timeId == null || timeId.isBlank() ||
                themeId == null || themeId.isBlank() ||
                paymentKey == null || paymentKey.isBlank() ||
                orderId == null || orderId.isBlank() ||
                amount == null
        ) {
            throw new InvalidRequestBodyException();
        }
        try {
            LocalDate.parse(date);
        } catch (DateTimeException e) {
            throw new InvalidRequestBodyException();
        }
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
