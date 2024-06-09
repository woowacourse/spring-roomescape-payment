package roomescape.domain.reservation.repository;

import roomescape.domain.reservation.model.ReservationWaitingWithOrder;

import java.util.List;
import java.util.Optional;

public interface CustomReservationWaitingRepository {
    List<ReservationWaitingWithOrder> findAllReservationWaitingWithOrdersByMemberId(Long memberId);

    Optional<ReservationWaitingWithOrder> findReservationWaitingWithOrder(Long reservationWaitingId);
}
