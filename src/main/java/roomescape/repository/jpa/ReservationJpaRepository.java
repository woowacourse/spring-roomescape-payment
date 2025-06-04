package roomescape.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.ReservationItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    @Query(value = """
            SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
            FROM reservation r
            JOIN reservation_item ri ON r.reservation_item_id = ri.id
            WHERE ri.date = :date 
            AND ri.time_id = :timeId 
            AND ri.theme_id = :themeId
            """, nativeQuery = true)
    boolean existsByDateAndTimeIdAndThemeId(
            @Param("date") LocalDate date,
            @Param("timeId") long timeId,
            @Param("themeId") long themeId
    );

    @Query("""
            SELECT COUNT(r)
            FROM Reservation r
            WHERE r.reservationItem.id = :reservationItemId
            AND r.id < :currentReservationId
            """)
    long countByReservationItemIdAndIdLessThan(
            @Param("reservationItemId") Long reservationItemId,
            @Param("currentReservationId") Long currentReservationId
    );

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE (:memberId IS NULL OR r.member.id = :memberId)
            AND (:themeId IS NULL OR r.reservationItem.theme.id = :themeId)
            AND (:dateFrom IS NULL OR r.reservationItem.date >= :dateFrom)
            AND (:dateTo IS NULL OR r.reservationItem.date <= :dateTo)
            """)
    List<Reservation> findByMemberIdAndThemeIdAndDateFromAndDateTo(
            @Param("memberId") Long memberId,
            @Param("themeId") Long themeId,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo
    );

    boolean existsByMemberAndReservationItem(Member member, ReservationItem reservationItem);

    Optional<Reservation> findFirstByReservationItemAndReservationStatusOrderByIdAsc(ReservationItem reservationItem, ReservationStatus reservationStatus);

    List<Reservation> findByReservationStatusOrderByIdDesc(ReservationStatus reservationStatus);

    List<Reservation> findByMemberId(Long memberId);

    boolean existsByReservationItem_Theme_Id(Long themeId);

    boolean existsByReservationItem_Time_Id(Long timeId);
}
