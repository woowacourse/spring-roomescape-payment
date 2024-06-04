package roomescape.payment.dto;

import roomescape.reservation.dto.MemberReservationCreateRequest;

import java.math.BigDecimal;

public record PaymentRequest(String paymentKey, String orderId, BigDecimal amount) {

    public static PaymentRequest from(MemberReservationCreateRequest request) {
        return new PaymentRequest(request.paymentKey(), request.orderId(), request.amount());
    }
}
