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
            WHERE r.member.id = :memberId
            AND r.status = 'RESERVED'
            """)
    List<Reservation> findAllReservedByMemberId(Long memberId);

    @Query("""
            SELECT new roomescape.reservation.domain.ReservationWaiting(r1.id,
                                                                            r1.member,
                                                                            r1.date.value,
                                                                            r1.time,
                                                                            r1.theme,
                                                                            r1.status,
                                                                            r1.createdAt)
            FROM Reservation AS r1
            WHERE r1.status = 'WAITING'
                AND r1.date.value = :date
                AND r1.time.id = :timeId
                AND r1.theme.id = :themeId
            ORDER BY r1.createdAt ASC
            """)
    List<ReservationWaiting> findAllReservationWaitingByDateAndTimeAndTheme(LocalDate date, Long timeId, Long themeId);

    @Query("""
            SELECT new roomescape.reservation.domain.ReservationWaiting(r1.id,
                                                                            r1.member,
                                                                            r1.date.value,
                                                                            r1.time,
                                                                            r1.theme,
                                                                            r1.status,
                                                                            r1.createdAt)
            FROM Reservation AS r1
            WHERE r1.status = 'WAITING'
                AND r1.member.id = :memberId
            ORDER BY r1.createdAt ASC
            """)
    List<ReservationWaiting> findAllReservationWaitingByMemberId(Long memberId);

    @Query("""
            SELECT new roomescape.reservation.domain.ReservationPending(r1.id,
                                                                            r1.member,
                                                                            r1.date.value,
                                                                            r1.time,
                                                                            r1.theme,
                                                                            r1.status,
                                                                            r1.createdAt)
            FROM Reservation AS r1
            WHERE r1.status = 'PENDING'
                AND r1.member.id = :memberId
            ORDER BY r1.createdAt ASC
            """)
    List<ReservationPending> findAllReservationPendingByMemberId(Long memberId);

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

    Optional<Reservation> findFirstByDateValueAndTimeIdAndThemeIdAndStatus(LocalDate date,
                                                                           Long timeId,
                                                                           Long themeId,
                                                                           Status status);

    boolean existsByDateValueAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId);

    boolean existsByDateValueAndTimeIdAndThemeIdAndMemberId(LocalDate date, Long timeId, Long themeId, Long memberId);
}
