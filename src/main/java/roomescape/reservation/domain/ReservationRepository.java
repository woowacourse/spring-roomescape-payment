package roomescape.reservation.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMemberIdAndThemeIdAndDateValueBetween(Long memberId,
                                                                  Long themeId,
                                                                  LocalDate dateFrom,
                                                                  LocalDate dateTo);

    List<Reservation> findByDateValueAndThemeId(LocalDate date, Long themeId);

    @Query("""
            SELECT new roomescape.reservation.domain.Reservation(r.id,
                                                                 r.member,
                                                                 r.date.value,
                                                                 r.time,
                                                                 r.theme,
                                                                 r.status,
                                                                 r.createdAt)
            FROM Reservation AS r
            WHERE r.member.id = :memberId AND r.status = 'RESERVED'
            """)
    List<Reservation> findAllReservedByMemberId(Long memberId);

    @Query("""
            SELECT new roomescape.reservation.domain.ReservationWaiting(r1.id,
                                                                            r1.member,
                                                                            r1.date.value,
                                                                            r1.time,
                                                                            r1.theme,
                                                                            r1.status,
                                                                            r1.createdAt,
                                                                            COUNT(r2.id))
            FROM Reservation AS r1
            LEFT JOIN Reservation AS r2
                ON r1.date.value = r2.date.value
                AND r1.time.id = r2.time.id
                AND r1.theme.id = r2.theme.id
                AND r1.createdAt > r2.createdAt
            WHERE r1.status = 'WAITING'
                AND r1.member.id = :memberId
            GROUP BY r1.id, r1.member, r1.date.value, r1.time, r1.theme, r1.status, r1.createdAt
            ORDER BY r1.createdAt ASC
            """)
    List<ReservationWaiting> findAllReservationWaitingByMemberId(Long memberId);

    @Query("""
            SELECT new roomescape.reservation.domain.ReservationWaiting(r.id,
                                                                        r.member,
                                                                        r.date.value,
                                                                        r.time,
                                                                        r.theme,
                                                                        r.status,
                                                                        r.createdAt)
            FROM Reservation AS r
            WHERE r.status = :status
            """)
    List<ReservationWaiting> findAllReservationByStatus(@Param("status") Status status);

    @Query("""
            SELECT new roomescape.reservation.domain.ReservationWithPayment(
                r.id,
                r.theme.name,
                r.date.value,
                r.time.startAt,
                r.status,
                p.paymentKey,
                p.totalAmount
          )
            FROM Reservation AS r
            LEFT JOIN Payment AS p
            ON r.id = p.reservationId
            WHERE r.member.id = :memberId AND r.status != 'WAITING'
            """)
    List<ReservationWithPayment> findAllNotWaitingReservationByMemberId(Long memberId);

    Optional<Reservation> findFirstByDateValueAndTimeIdAndThemeIdAndStatus(LocalDate date,
                                                                           Long timeId,
                                                                           Long themeId,
                                                                           Status status);

    boolean existsByDateValueAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId);

    boolean existsByDateValueAndTimeIdAndThemeIdAndMemberId(LocalDate date, Long timeId, Long themeId, Long memberId);
}
