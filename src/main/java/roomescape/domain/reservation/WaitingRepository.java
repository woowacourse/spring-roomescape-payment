package roomescape.domain.reservation;

import java.util.List;
import java.util.Optional;
import roomescape.dto.reservation.WaitingWithRank;

public interface WaitingRepository {

    boolean existsByReservationIdAndMemberId(Long reservationId, Long memberId);

    Waiting save(Waiting withoutId);

    void deleteById(Long id);

    List<Waiting> findAll();

    List<Waiting> findAllByReservationId(Long reservationId);

    List<WaitingWithRank> findAllWithRankByMemberId(Long memberId);

    Optional<Waiting> findById(Long id);
}
