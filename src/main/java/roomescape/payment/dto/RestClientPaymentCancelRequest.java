package roomescape.payment.dto;

import roomescape.payment.domain.CancelReason;

public record RestClientPaymentCancelRequest(CancelReason cancelReason) {
}
