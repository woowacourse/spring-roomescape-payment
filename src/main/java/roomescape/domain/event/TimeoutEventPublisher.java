package roomescape.domain.event;


import roomescape.domain.reservation.Reservation;

public interface TimeoutEventPublisher {

    void publishTimeoutEvent(Reservation reservation);

}
