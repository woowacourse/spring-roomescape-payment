package roomescape.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.WaitingRepository;
import roomescape.dto.reservation.WaitingWithRank;
import roomescape.infrastructure.persistence.jpa.WaitingJpaRepository;

@Repository
@RequiredArgsConstructor
public class WaitingRepositoryImpl implements WaitingRepository {

    private final WaitingJpaRepository waitingJpaRepository;

    @Override
    public boolean existsByReservationIdAndMemberId(final Long reservationId, final Long memberId) {
        return waitingJpaRepository.existsByReservationIdAndMemberId(reservationId, memberId);
    }

    @Override
    public Waiting save(final Waiting waiting) {
        return waitingJpaRepository.save(waiting);
    }

    @Override
    public void deleteById(final Long id) {
        waitingJpaRepository.deleteById(id);
    }

    @Override
    public List<Waiting> findAll() {
        return waitingJpaRepository.findAll();
    }

    @Override
    public List<Waiting> findAllByReservationId(final Long reservationId) {
        return waitingJpaRepository.findAllByReservationIdOrderById(reservationId);
    }

    @Override
    public List<WaitingWithRank> findAllWithRankByMemberId(final Long memberId) {
        return waitingJpaRepository.findAllByMemberId(memberId);
    }

    @Override
    public Optional<Waiting> findById(final Long id) {
        return waitingJpaRepository.findById(id);
    }
}
