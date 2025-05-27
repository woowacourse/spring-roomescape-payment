package roomescape.reservation.client;

public record PaymentsConfirmResponse(String paymentKey,
                                      long totalAmount) {
}
