package roomescape.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationItemRepository;
import roomescape.repository.jpa.ReservationItemJpaRepository;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ReservationItemRepositoryImpl implements ReservationItemRepository {

    private final ReservationItemJpaRepository reservationItemJpaRepository;

    @Override
    public ReservationItem save(ReservationItem reservationItem) {
        return reservationItemJpaRepository.save(reservationItem);
    }

    @Override
    public void delete(ReservationItem reservationItem) {
        reservationItemJpaRepository.delete(reservationItem);
    }

    @Override
    public Optional<ReservationItem> findReservationItemByDateAndTimeIdAndThemeId(final LocalDate date, final Long timeId, final Long themeId) {
        return reservationItemJpaRepository.findReservationItemByDateAndTime_IdAndTheme_Id(date, timeId, themeId);
    }

    @Override
    public boolean existsByDateAndTimeAndTheme(LocalDate date, Long timeId, Long themeId) {
        return reservationItemJpaRepository.existsByDateAndTime_IdAndTheme_Id(date, timeId, themeId);
    }

    @Override
    public void deleteAllReservationItemByThemeId(long themeId) {
        reservationItemJpaRepository.deleteAllByTheme_Id(themeId);
    }

    @Override
    public void deleteAllReservationItemByTimeId(long timeId) {
        reservationItemJpaRepository.deleteAllByTime_Id(timeId);
    }
}
