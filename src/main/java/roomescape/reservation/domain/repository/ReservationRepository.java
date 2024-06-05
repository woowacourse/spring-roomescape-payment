package roomescape.reservation.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.WaitingWithRank;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    List<Reservation> findByReservationTime(ReservationTime reservationTime);

    List<Reservation> findByThemeId(Long themeId);

    @Query("SELECT new roomescape.reservation.domain.WaitingWithRank(" +
            "    w, " +
            "    (SELECT COUNT(w2) " +
            "     FROM Reservation w2 " +
            "     WHERE w2.theme = w.theme " +
            "       AND w2.date = w.date " +
            "       AND w2.reservationTime = w.reservationTime " +
            "       AND w2.id < w.id)) " +
            "FROM Reservation w " +
            "WHERE w.member.id = :memberId")
    List<WaitingWithRank> findWaitingsWithRankByMemberId(Long memberId);

    @Modifying
    @Query("""
            UPDATE Reservation r
            SET r.reservationStatus = :status
            WHERE r.id = :id
            """)
    int updateStatusByReservationId(@Param(value = "id") Long reservationId,
                                    @Param("status") ReservationStatus statusForChange);

    @Query("""
            SELECT EXISTS (
               SELECT 1 FROM Reservation r
               WHERE r.theme.id = r2.theme.id
                 AND r.reservationTime.id = r2.reservationTime.id
                 AND r.date = r2.date
                 AND r.reservationStatus = 'CONFIRMED'
            )
            FROM Reservation r2
            WHERE r2.id = :id AND r2.reservationStatus = 'WAITING'
            """)
    boolean isExistConfirmedReservation(@Param("id") Long reservationId);
}
