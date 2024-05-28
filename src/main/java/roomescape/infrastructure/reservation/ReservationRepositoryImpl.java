package roomescape.infrastructure.reservation;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import roomescape.domain.dto.ReservationWithRank;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.schedule.ReservationDate;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    public ReservationRepositoryImpl(ReservationJpaRepository reservationJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public List<Reservation> findBy(Long memberId, Long themeId, ReservationDate dateFrom, ReservationDate dateTo) {
        return reservationJpaRepository.findBy(memberId, themeId, dateFrom, dateTo);
    }

    @Override
    public boolean existsByDetailIdAndMemberId(Long reservationDetailId, Long memberId) {
        return reservationJpaRepository.existsByDetailIdAndMemberId(reservationDetailId, memberId);
    }

    @Override
    public boolean existsByDetailIdAndStatus(Long reservationDetailId, ReservationStatus status) {
        return reservationJpaRepository.existsByDetailIdAndStatus(reservationDetailId, status);
    }

    @Override
    public boolean existsByDetailThemeId(long themeId) {
        return reservationJpaRepository.existsByDetailThemeId(themeId);
    }

    @Override
    public boolean existsByDetailScheduleTimeId(long timeId) {
        return reservationJpaRepository.existsByDetailScheduleTimeId(timeId);
    }

    @Override
    public Optional<Reservation> findFirstByDetailIdOrderByCreatedAt(long detailId) {
        return reservationJpaRepository.findFirstByDetailIdOrderByCreatedAt(detailId);
    }

    @Override
    public List<Reservation> findAllByStatus(ReservationStatus status) {
        return reservationJpaRepository.findAllByStatus(status);
    }

    @Override
    public List<ReservationWithRank> findWithRankingByMemberId(long memberId) {
        return reservationJpaRepository.findWithRankingByMemberId(memberId);
    }

    @Override
    public Optional<Reservation> findById(long id) {
        return reservationJpaRepository.findById(id);
    }

    @Override
    public void deleteById(long id) {
        reservationJpaRepository.deleteById(id);
    }
}
