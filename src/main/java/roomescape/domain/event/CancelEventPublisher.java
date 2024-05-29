package roomescape.domain.event;


import roomescape.domain.reservation.Reservation;

public interface CancelEventPublisher {

    void publishPaymentPendingEvent(Reservation reservation);

}
