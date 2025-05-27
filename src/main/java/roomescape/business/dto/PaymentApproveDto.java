package roomescape.business.dto;

public record PaymentApproveDto(
    String paymentKey,
    String orderId,
    Long amount
)
{}
