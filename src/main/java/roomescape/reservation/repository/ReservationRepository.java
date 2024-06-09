package roomescape.reservation.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.common.exception.EntityNotExistException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.ReservationWithPaymentResponse;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Override
    @EntityGraph(attributePaths = {"member", "theme", "time"})
    List<Reservation> findAll();

    @Override
    @EntityGraph(attributePaths = {"member", "theme", "time"})
    Optional<Reservation> findById(Long id);

    @Query("""
            select r.time.id from Reservation r 
            join fetch ReservationTime rt on r.time.id = rt.id  
            where r.date = :date and r.theme.id = :themeId
            """)
    List<Long> findTimeIdsByDateAndThemeId(LocalDate date, Long themeId);

    @Query("""
            select r from Reservation r
            where r.date = :date
            and r.status = 'WAIT'
            and r.time.id = :timeId
            and r.theme.id = :themeId
            order by r.createdAt
            limit 1
            """)
    Optional<Reservation> findFirstWaitingReservationBy(LocalDate date, Long timeId, Long themeId);

    @EntityGraph(attributePaths = {"member", "theme", "time"})
    List<Reservation> findAllByThemeIdAndMemberIdAndDateBetweenOrderByDateAscTimeStartAtAscCreatedAtAsc(
            Long themeId,
            Long memberId,
            LocalDate dateFrom,
            LocalDate dateTo
    );

    @Query("""
           select r from Reservation r
           join fetch ReservationTime rt on rt.id = r.time.id
           join fetch Theme t on t.id = r.theme.id
           join fetch Member m on m.id = r.member.id
           where r.status = :status and r.date >= :date
           order by r.date, rt.startAt, r.createdAt
            """)
    List<Reservation> findAllByStatusFromDate(ReservationStatus status, LocalDate date);

    @EntityGraph(attributePaths = {"time"})
    boolean existsByThemeAndDateAndTimeStartAtAndStatus(Theme theme, LocalDate date, LocalTime startAt, ReservationStatus status);

    @Query("""
           select r.status from Reservation r
           join ReservationTime rt on r.time.id = rt.id
           where r.member.id = :memberId and r.theme = :theme and r.date = :date and rt.startAt = :startAt
            """)
    List<ReservationStatus> findStatusesByMemberIdAndThemeAndDateAndTimeStartAt(Long memberId, Theme theme, LocalDate date, LocalTime startAt);

    @Query("""
        select new roomescape.reservation.dto.ReservationWithPaymentResponse(
            r.id,
            t,
            r.date,
            rt,
            r.status,
            coalesce(p.paymentKey, ''),
            coalesce(p.totalAmount, 0)
        ) from Reservation r
        join ReservationTime rt on rt.id = r.time.id
        join Member m on m.id = r.member.id
        join Theme t on t.id = r.theme.id
        left join Payment p on p.reservation.id = r.id
        where m.id = :memberId and r.date >= :date
        order by r.date, rt.startAt, r.createdAt
""")
    List<ReservationWithPaymentResponse> findAllMemberReservationWithPayment(Long memberId, LocalDate date);

    default Reservation fetchById(Long id) {
        return findById(id).orElseThrow(() -> new EntityNotExistException("존재하지 않는 예약입니다."));
    }
}
