package roomescape.reservation.service;

import roomescape.reservation.domain.PaymentType;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.service.dto.PaymentRequest;

public interface PaymentService<T extends PaymentRequest> {
    void createPayment(T request, Reservation reservation);
    PaymentType getType();
}
