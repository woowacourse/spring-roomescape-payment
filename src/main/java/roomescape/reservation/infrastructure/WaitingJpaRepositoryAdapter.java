package roomescape.reservation.infrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.domain.WaitingRepository;
import roomescape.reservation.dto.WaitingWithRank;

@Repository
public class WaitingJpaRepositoryAdapter implements WaitingRepository {

    private final WaitingJpaRepository waitingJpaRepository;

    public WaitingJpaRepositoryAdapter(final WaitingJpaRepository waitingJpaRepository) {
        this.waitingJpaRepository = waitingJpaRepository;
    }

    @Override
    public boolean existsByReservationIdAndMemberId(Long reservationId, Long memberId) {
        return waitingJpaRepository.existsByReservationIdAndMemberId(reservationId, memberId);
    }

    @Override
    public Waiting save(Waiting waiting) {
        return waitingJpaRepository.save(waiting);
    }

    @Override
    public List<WaitingWithRank> findByMemberId(Long memberId) {
        return waitingJpaRepository.findByMemberId(memberId);
    }

    @Override
    public Optional<Waiting> findById(Long id) {
        return waitingJpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        waitingJpaRepository.deleteById(id);
    }

    @Override
    public List<Waiting> findAll() {
        return waitingJpaRepository.findAll();
    }

    @Override
    public List<Waiting> findByReservationId(Long reservationId) {
        return waitingJpaRepository.findByReservationIdOrderById(reservationId);
    }
}
