package roomescape.paymenthistory.dto;

import roomescape.reservation.domain.Reservation;

public record PaymentCreateRequest(String paymentKey, String orderId, int amount, Reservation Reservation) {
    public RestClientPaymentApproveRequest createRestClientPaymentApproveRequest() {
        return new RestClientPaymentApproveRequest(paymentKey, orderId, amount);
    }
}
