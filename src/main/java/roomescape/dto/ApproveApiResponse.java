package roomescape.dto;

public record ApproveApiResponse(String orderId, String paymentKey, long totalAmount) {
}
