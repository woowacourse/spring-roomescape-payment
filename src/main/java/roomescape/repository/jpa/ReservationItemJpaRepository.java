package roomescape.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationitem.ReservationItem;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReservationItemJpaRepository extends JpaRepository<ReservationItem, Long> {

    Optional<ReservationItem> findReservationItemByDateAndTime_IdAndTheme_Id(LocalDate date, Long timeId, Long themeId);

    boolean existsByDateAndTime_IdAndTheme_Id(LocalDate date, Long timeId, Long themeId);

    @Modifying(flushAutomatically = true)
    void deleteAllByTheme_Id(long themeId);

    @Modifying(flushAutomatically = true)
    void deleteAllByTime_Id(long timeId);
}
