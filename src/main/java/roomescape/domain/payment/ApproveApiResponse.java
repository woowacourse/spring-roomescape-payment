package roomescape.domain.payment;

record ApproveApiResponse(String orderId, String paymentKey, long totalAmount) {
}
