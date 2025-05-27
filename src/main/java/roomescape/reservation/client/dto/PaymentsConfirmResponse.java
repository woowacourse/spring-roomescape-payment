package roomescape.reservation.client.dto;

public record PaymentsConfirmResponse(String paymentKey,
                                      long totalAmount) {
}
