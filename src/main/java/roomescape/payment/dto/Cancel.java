package roomescape.payment.dto;

public record Cancel(String cancelReason, String canceledAt, String cancelAmount) {
}
