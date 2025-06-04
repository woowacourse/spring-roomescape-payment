package roomescape.reservation.infrastructure;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;

@Repository
public class ReservationJpaRepositoryAdapter implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    public ReservationJpaRepositoryAdapter(ReservationJpaRepository reservationJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    public Reservation save(final Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public void deleteById(final Long id) {
        reservationJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Reservation> findById(final Long id) {
        return reservationJpaRepository.findById(id);
    }

    @Override
    public List<Reservation> findAllByDateAndThemeId(final LocalDate date, final Long themeId) {
        return reservationJpaRepository.findByDateAndThemeId(date, themeId);
    }

    @Override
    public List<Reservation> findAllByMemberIdAndThemeIdAndDateBetween(final Long memberId, final Long themeId, final LocalDate from, final LocalDate to) {
        return reservationJpaRepository.findByMemberIdAndThemeIdAndDateBetween(memberId, themeId, from, to);
    }

    @Override
    public List<Reservation> findAllByMemberId(Long memberId) {
        return reservationJpaRepository.findByMemberId(memberId);
    }

    @Override
    public List<Reservation> findAll() {
        return reservationJpaRepository.findAll();
    }

    @Override
    public boolean existsByTimeId(final Long reservationTimeId) {
        return reservationJpaRepository.existsByTimeId(reservationTimeId);
    }

    @Override
    public boolean existByThemeId(final Long themeId) {
        return reservationJpaRepository.existsByThemeId(themeId);
    }

    @Override
    public Optional<Reservation> findBy(LocalDate date, Long timeId, Long themeId) {
        return reservationJpaRepository.findByDateAndTimeIdAndThemeId(date, timeId, themeId);
    }
}
