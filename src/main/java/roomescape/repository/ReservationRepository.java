package roomescape.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.entity.Theme;
import roomescape.global.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = """
                select r
                from Reservation r
                join fetch r.member
                join fetch r.reservationTime
                join fetch r.theme
                left join fetch r.payment
                where r.date = :date
                and r.theme.id = :themeId
                and (r.status = :reserved or r.status = :pending)
            """)
    List<Reservation> findAllAlreadyReservedReservation(@Param("date") LocalDate date,
                                                        @Param("themeId") Long themeId,
                                                        @Param("reserved") ReservationStatus reserved,
                                                        @Param("pending") ReservationStatus pending);

    List<Reservation> findAllByDateBetween(LocalDate start, LocalDate end);

    @Query(value = """
            select r from Reservation r
            join fetch r.member
            join fetch r.reservationTime
            join fetch r.theme
            left join fetch r.payment
            where (:memberId is null or r.member.id  = :memberId)
            and (:themeId is null or r.theme.id = :themeId)
            and (:dateFrom is null or r.date >= :dateFrom)
            and (:dateTo is null or r.date <= :dateTo)
            """)
    List<Reservation> findAllByFilter(@Param("memberId") Long memberId,
                                      @Param("themeId") Long themeId,
                                      @Param("dateFrom") LocalDate dateFrom,
                                      @Param("dateTo") LocalDate dateTo);

    @Query(value = """
            select r
            from Reservation r
            join fetch r.member
            join fetch r.reservationTime
            join fetch r.theme
            left join fetch r.payment
            where r.status = :status
            """)
    List<Reservation> findAllFetchByStatus(@Param("status") ReservationStatus status);

    Optional<Reservation> findByDateAndReservationTimeAndThemeAndStatus(LocalDate date,
                                                                        ReservationTime reservationTime,
                                                                        Theme theme,
                                                                        ReservationStatus status);

    boolean existsByReservationTimeId(Long timeId);

    boolean existsByThemeId(Long themeId);

    @Query(value = """
                select exists (
                    select r
                    from Reservation r
                    where r.date = :date
                    and r.reservationTime.id = :timeId
                    and r.theme.id = :themeId
                    and (r.status = "RESERVED" or r.status = "PENDING")
                )
            """)
    boolean existsAlreadyReservedReservation(@Param("date") LocalDate date,
                                             @Param("timeId") Long timeId,
                                             @Param("themeId") Long themeId,
                                             @Param("reserved") ReservationStatus reserved,
                                             @Param("pending") ReservationStatus pending);

    @Query(value = """
            select r
            from Reservation r
            join fetch r.member
            join fetch r.reservationTime
            join fetch r.theme
            left join fetch r.payment
            """)
    List<Reservation> findAllFetch();
}
