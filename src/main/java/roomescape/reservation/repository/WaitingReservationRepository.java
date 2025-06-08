package roomescape.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.reservation.domain.WaitingReservation;

public interface WaitingReservationRepository extends JpaRepository<WaitingReservation, Long> {

    @Query("""
        select w from WaitingReservation w
        join fetch w.registrationSlot bs
        join fetch bs.time wt
        join fetch bs.theme wth
        join fetch w.member m
    """)
    List<WaitingReservation> findAllWithDetail();

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
