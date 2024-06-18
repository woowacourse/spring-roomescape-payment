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
import roomescape.reservation.dto.response.MyReservationResponse;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    List<Reservation> findByReservationTime(ReservationTime reservationTime);

    List<Reservation> findByThemeId(Long themeId);

    @Modifying
    @Query("""
            UPDATE Reservation r
            SET r.reservationStatus = :status
            WHERE r.id = :id
            """)
    int updateStatusByReservationId(@Param(value = "id") Long reservationId,
                                    @Param(value = "status") ReservationStatus statusForChange);

    @Query("""
            SELECT EXISTS (
               SELECT 1 FROM Reservation r
               WHERE r.theme.id = r2.theme.id
                 AND r.reservationTime.id = r2.reservationTime.id
                 AND r.date = r2.date
                 AND r.reservationStatus != 'WAITING'
            )
            FROM Reservation r2
            WHERE r2.id = :id
            """)
    boolean isExistConfirmedReservation(@Param("id") Long reservationId);

    @Query("""
            SELECT new roomescape.reservation.dto.response.MyReservationResponse(
                r.id,
                t.name,
                r.date,
                r.reservationTime.startAt,
                r.reservationStatus,
                (SELECT COUNT (r2) FROM Reservation r2 WHERE r2.theme = r.theme AND r2.date = r.date AND r2.reservationTime = r.reservationTime AND r2.id < r.id),
                p.paymentKey,
                p.totalAmount
            )
            FROM Reservation r
            JOIN r.theme t
            LEFT JOIN Payment p
            ON p.reservation = r
            WHERE r.member.id = :memberId
            """)
    List<MyReservationResponse> findMyReservations(Long memberId);
}
