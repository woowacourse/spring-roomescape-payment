package roomescape.client.dto;

public record PaymentsConfirmResponse(String paymentKey,
                                      String orderId,
                                      long totalAmount) {
}
