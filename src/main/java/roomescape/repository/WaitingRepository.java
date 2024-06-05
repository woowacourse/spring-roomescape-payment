package roomescape.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.waiting.Waiting;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    default Waiting findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new RoomEscapeException(
                ErrorCode.WAITING_NOT_FOUND_BY_ID,
                "waiting_id = " + id
        ));
    }

    default Waiting findByReservationIdOrThrow(Long reservationId) {
        return findByReservationId(reservationId).orElseThrow(() -> new RoomEscapeException(
                ErrorCode.WAITING_NOT_FOUND_BY_RESERVATION,
                "reservation_id = " + reservationId
        ));
    }

    Optional<Waiting> findByReservationId(Long id);
}
