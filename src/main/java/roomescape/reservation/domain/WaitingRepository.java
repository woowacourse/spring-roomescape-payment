package roomescape.reservation.domain;

import java.util.List;
import java.util.Optional;
import roomescape.reservation.dto.WaitingWithRank;

public interface WaitingRepository {

    boolean existsByReservationIdAndMemberId(Long reservationId, Long memberId);

    Waiting save(Waiting withoutId);

    List<WaitingWithRank> findByMemberId(Long memberId);

    Optional<Waiting> findById(Long id);

    void deleteById(Long id);

    List<Waiting> findAll();

    List<Waiting> findByReservationId(Long reservationId);
}
