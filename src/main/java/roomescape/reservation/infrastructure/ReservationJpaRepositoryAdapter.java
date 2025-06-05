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

    public ReservationJpaRepositoryAdapter(final ReservationJpaRepository reservationJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public void deleteById(Long id) {
        reservationJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationJpaRepository.findById(id);
    }

    @Override
    public List<Reservation> findAllByDateAndThemeId(LocalDate date, Long themeId) {
        return reservationJpaRepository.findByDateAndThemeId(date, themeId);
    }

    @Override
    public List<Reservation> findAllByMemberIdAndThemeIdAndDateBetween(Long memberId, Long themeId, LocalDate from,
                                                                       LocalDate to) {
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
    public boolean existsByTimeId(Long reservationTimeId) {
        return reservationJpaRepository.existsByTimeId(reservationTimeId);
    }

    @Override
    public boolean existByThemeId(Long themeId) {
        return reservationJpaRepository.existsByThemeId(themeId);
    }

    @Override
    public Optional<Reservation> findBy(LocalDate date, Long timeId, Long themeId) {
        return reservationJpaRepository.findByDateAndTimeIdAndThemeId(date, timeId, themeId);
    }
}
