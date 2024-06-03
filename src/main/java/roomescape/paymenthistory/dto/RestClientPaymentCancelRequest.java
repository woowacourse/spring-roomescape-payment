package roomescape.paymenthistory.dto;

import roomescape.paymenthistory.domain.CancelReason;

public record RestClientPaymentCancelRequest(CancelReason cancelReason) {
}
