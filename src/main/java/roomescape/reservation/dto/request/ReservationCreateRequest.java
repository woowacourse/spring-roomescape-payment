package roomescape.reservation.dto.request;

import java.time.LocalDate;

public record ReservationCreateRequest(ReservationRequest reservation, PaymentRequest payment) {

    public Long getThemeId() {
        return reservation.themeId();
    }

    public Long getTimeId() {
        return reservation.timeId();
    }

    public LocalDate getDate() {
        return reservation.date();
    }

    public String getPaymentKey() {
        return payment.paymentKey();
    }

    public String getOrderId() {
        return payment.orderId();
    }

    public Integer getAmount() {
        return payment().amount();
    }

    public String getPaymentType() {
        return payment.paymentType();
    }

}
