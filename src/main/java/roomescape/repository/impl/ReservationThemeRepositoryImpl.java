package roomescape.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationThemeRepository;
import roomescape.repository.jpa.ReservationJpaRepository;
import roomescape.repository.jpa.ReservationThemeJpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ReservationThemeRepositoryImpl implements ReservationThemeRepository {

    private final ReservationThemeJpaRepository reservationThemeJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Optional<ReservationTheme> findById(final Long id) {
        return reservationThemeJpaRepository.findById(id);
    }

    @Override
    public List<ReservationTheme> findAll() {
        return reservationThemeJpaRepository.findAll();
    }

    @Override
    public List<ReservationTheme> findWeeklyThemeOrderByCountDesc(int amount, LocalDate dateFrom, LocalDate dateTo) {
        return reservationThemeJpaRepository.findPopularThemesByRankAndDuration(amount, dateFrom, dateTo);
    }

    @Override
    public ReservationTheme save(final ReservationTheme reservationTheme) {
        return reservationThemeJpaRepository.save(reservationTheme);
    }

    @Override
    public void deleteById(final long id) {
        reservationThemeJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(final String name) {
        return reservationThemeJpaRepository.existsByName(name);
    }

    @Override
    public boolean isAvailableToRemove(final long id) {
        return !reservationJpaRepository.existsByReservationItem_Theme_Id(id);
    }

    @Override
    public boolean existsById(final long id) {
        return reservationThemeJpaRepository.existsById(id);
    }
}
