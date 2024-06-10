package roomescape.reservation.dto;

import roomescape.payment.dto.PaymentResponse;

public record ReservationPaymentDetail(ReservationDetailResponse reservationDetailResponse,
                                       PaymentResponse paymentResponse) {
}
