package roomescape.domain;

public record CancelPayment(Payment payment, CancelReason cancelReason) {
}
