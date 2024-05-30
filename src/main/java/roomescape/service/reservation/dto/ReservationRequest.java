package roomescape.service.reservation.dto;

import java.time.DateTimeException;
import java.time.LocalDate;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.exception.common.InvalidRequestBodyException;
import roomescape.service.payment.dto.PaymentConfirmRequest;

public class ReservationRequest {
    private final LocalDate date;
    private final Long timeId;
    private final Long themeId;
    private final String paymentKey;
    private final String orderId;
    private final Integer amount;

    public ReservationRequest(
            String date, String timeId, String themeId, String paymentKey, String orderId, Integer amount) {
        validate(date, timeId, themeId, paymentKey, orderId, amount);
        this.date = LocalDate.parse(date);
        this.timeId = Long.parseLong(timeId);
        this.themeId = Long.parseLong(themeId);
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public void validate(
            String date, String timeId, String themeId, String paymentKey, String orderId, Integer amount) {
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

    public Reservation toReservation(ReservationTime reservationTime, Theme theme, Member member) {
        return new Reservation(date, reservationTime, theme, member);
    }

    public PaymentConfirmRequest toPaymentRequest() {
        return new PaymentConfirmRequest(orderId, amount, paymentKey);
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
