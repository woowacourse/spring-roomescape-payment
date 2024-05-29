package roomescape.paymenthistory.dto;

import roomescape.reservation.domain.Reservation;

// TODO: 도메인으로 분리
public record PaymentCreateRequest(String paymentKey, String orderId, int amount, Reservation Reservation) {
    public RestClientPaymentApproveRequest createRestClientPaymentApproveRequest() {
        return new RestClientPaymentApproveRequest(paymentKey, orderId, amount);
    }
}
