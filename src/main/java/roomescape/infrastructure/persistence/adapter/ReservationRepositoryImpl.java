package roomescape.infrastructure.persistence.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.dto.reservation.ReservationWithPayment;
import roomescape.infrastructure.persistence.jpa.ReservationJpaRepository;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public boolean existsByTimeId(final Long reservationTimeId) {
        return reservationJpaRepository.existsByTimeId(reservationTimeId);
    }

    @Override
    public boolean existByThemeId(final Long themeId) {
        return reservationJpaRepository.existsByThemeId(themeId);
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
    public List<Reservation> findAll() {
        return reservationJpaRepository.findAll();
    }

    @Override
    public List<Reservation> findAllByDateAndThemeId(final LocalDate date, final Long themeId) {
        return reservationJpaRepository.findAllByDateAndThemeId(date, themeId);
    }

    @Override
    public List<Reservation> findAllByMemberIdAndThemeIdAndDateBetween(final Long memberId, final Long themeId, final LocalDate from, final LocalDate to) {
        return reservationJpaRepository.findAllByMemberIdAndThemeIdAndDateBetween(memberId, themeId, from, to);
    }

    @Override
    public List<ReservationWithPayment> findAllWithPaymentByMemberId(final Long memberId) {
        return reservationJpaRepository.findAllWithPaymentByMemberId(memberId);
    }

    @Override
    public List<Reservation> findAllWithoutPaymentByMemberId(final Long memberId) {
        return reservationJpaRepository.findAllWithoutPaymentByMemberId(memberId);
    }

    @Override
    public Optional<Reservation> findById(final Long id) {
        return reservationJpaRepository.findById(id);
    }

    @Override
    public Optional<Reservation> findBy(final LocalDate date, final Long timeId, final Long themeId) {
        return reservationJpaRepository.findByDateAndTimeIdAndThemeId(date, timeId, themeId);
    }
}
