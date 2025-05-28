package roomescape.reservation.dto.response;

public record TossPaymentResponse(
        String paymentKey,
        String orderId,
        String orderName,
        String status,
        String approvedAt,
        String method,
        int totalAmount,
        CardInfo card
) {
    public record CardInfo(
            String company,
            String number,
            int installmentPlanMonths,
            String approveNo
    ) {
    }
}
