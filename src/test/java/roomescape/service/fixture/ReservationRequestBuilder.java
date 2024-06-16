package roomescape.service.fixture;

import roomescape.request.ReservationRequest;

import java.time.LocalDate;

public class ReservationRequestBuilder {

    private LocalDate date = LocalDate.now().plusDays(99);
    private Long timeId = 1L;
    private Long themeId = 1L;
    private String orderId = "orderId";
    private String paymentKey = "paymentKey";
    private Long amount = 1999999L;

    public static ReservationRequestBuilder builder() {
        return new ReservationRequestBuilder();
    }

    public ReservationRequestBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public ReservationRequestBuilder timeId(Long timeId) {
        this.timeId = timeId;
        return this;
    }

    public ReservationRequestBuilder themeId(Long themeId) {
        this.themeId = themeId;
        return this;
    }


    public ReservationRequestBuilder orderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public ReservationRequestBuilder paymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
        return this;
    }

    public ReservationRequestBuilder amount(Long amount) {
        this.amount = amount;
        return this;
    }

    public ReservationRequest build() {
        return new ReservationRequest(date, timeId, themeId, orderId, paymentKey, amount);
    }
}
