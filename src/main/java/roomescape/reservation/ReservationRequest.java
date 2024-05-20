package roomescape.reservation;

public class ReservationRequest {
    private String name;
    private String date;
    private Long theme;
    private Long time;
    private String orderId;
    private String paymentKey;
    private Long amount;
    private String paymentType;

    public ReservationRequest() {
    }

    public ReservationRequest(String name, String date, Long theme, Long time,
                              String orderId, String paymentKey, Long amount, String paymentType) {
        this.name = name;
        this.date = date;
        this.theme = theme;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public Long getTheme() {
        return theme;
    }

    public Long getTime() {
        return time;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }

    public String getPaymentType() {
        return paymentType;
    }
}
