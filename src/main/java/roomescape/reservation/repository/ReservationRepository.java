package roomescape.reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.model.ReservationDate;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
                SELECT r FROM Reservation r
                JOIN r.time rt
                JOIN r.theme th
                JOIN r.member m
                WHERE (:memberId IS NULL OR m.id = :memberId)
                AND (:themeId IS NULL OR th.id = :themeId)
                AND (:from IS NULL OR r.date >= :from)
                AND (:to IS NULL OR r.date <= :to)
            """)
    List<Reservation> searchReservations(
            @Param("memberId") Long memberId,
            @Param("themeId") Long themeId,
            @Param("from") ReservationDate from,
            @Param("to") ReservationDate to
    );

    boolean existsByDateAndTimeIdAndThemeId(ReservationDate date, Long timeId, Long themeId);

    boolean existsByTimeId(Long reservationTimeId);

    boolean existsByThemeId(Long themeId);

    List<Reservation> findAllByDateAndThemeId(ReservationDate date, Long themeId);

    @Query("""
            SELECT new roomescape.reservation.dto.MyReservationResponse(r, p) FROM Reservation r, Payment p
            WHERE (:memberId IS NULL OR r.member.id = :memberId)
            AND r.payment.id = p.id
            """)
    List<MyReservationResponse> findAllByMemberIdWithPayment(Long memberId);

    Reservation findByDateAndThemeIdAndTimeId(ReservationDate date, Long themeId, Long timeId);
}
