package roomescape.infrastructure.persistence.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import roomescape.domain.reservation.Waiting;
import roomescape.dto.reservation.WaitingWithRank;

public interface WaitingJpaRepository extends CrudRepository<Waiting, Long> {

    boolean existsByReservationIdAndMemberId(Long reservationId, Long memberId);

    List<Waiting> findAll();

    @Query("""
            SELECT new roomescape.dto.reservation.WaitingWithRank(
                w,
                (SELECT COUNT(w2) + 1
                 FROM Waiting w2
                 WHERE w2.reservation = w.reservation
                     AND w2.id < w.id))
            FROM Waiting w
            WHERE w.member.id = :memberId
            """
    )
    List<WaitingWithRank> findAllByMemberId(@Param("memberId") Long memberId);

    List<Waiting> findAllByReservationIdOrderById(@Param("reservationId") Long reservationId);
}
