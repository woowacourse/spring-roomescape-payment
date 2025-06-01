package roomescape.client.dto;

public record PaymentsConfirmResponse(String paymentKey,
                                      long totalAmount) {
}
