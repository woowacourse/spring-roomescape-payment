package roomescape.domain.reservation.repository;

import roomescape.domain.reservation.model.ReservationWaitingWithOrder;

import java.util.List;

public interface CustomReservationWaitingRepository {
    List<ReservationWaitingWithOrder> findAllReservationWaitingWithOrdersByMemberId(Long memberId);
}
