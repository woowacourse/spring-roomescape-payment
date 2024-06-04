package roomescape.payment.model;

public record PaymentInfoFromClient(String paymentKey,
                                    String orderId,
                                    Long amount) {
}
