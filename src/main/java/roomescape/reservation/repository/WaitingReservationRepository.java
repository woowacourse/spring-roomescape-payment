package roomescape.reservation.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.reservation.domain.WaitingReservation;

public interface WaitingReservationRepository extends JpaRepository<WaitingReservation, Long> {

    @Query("""
    SELECT EXISTS (
        SELECT 1 FROM WaitingReservation wr
        WHERE wr.member.id = :memberId
          AND wr.registrationSlot.theme.id = :themeId
          AND wr.registrationSlot.time.id = :timeId
          AND wr.registrationSlot.date = :date
        )
    """)
    boolean memberHasWaitingAtSlot(Long memberId, Long themeId, Long timeId, LocalDate date);
}
