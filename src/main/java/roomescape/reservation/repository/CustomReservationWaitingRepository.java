package roomescape.reservation.repository;

import java.util.List;
import roomescape.reservation.model.ReservationWaitingWithOrder;

public interface CustomReservationWaitingRepository {
    List<ReservationWaitingWithOrder> findAllReservationWaitingWithOrdersByMemberId(Long memberId);
}
