package roomescape.application.payment.dto;

public record PaymentClientResponse(String paymentKey, String orderId, String status, long totalAmount) {

    private static final String STATUS_DONE = "DONE";

    public boolean isNotSuccessful() {
        return !STATUS_DONE.equals(status);
    }
}
