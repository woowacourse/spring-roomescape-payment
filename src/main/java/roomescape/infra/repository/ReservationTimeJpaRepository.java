package roomescape.infra.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import roomescape.domain.reservationdetail.ReservationTime;

public interface ReservationTimeJpaRepository extends Repository<ReservationTime, LocalTime> {

    ReservationTime save(ReservationTime time);

    Optional<ReservationTime> findById(Long id);

    List<ReservationTime> findAll();

    @Query("""
            select d.time from ReservationDetail d
            where d.date = :date
            and d.theme.id = :themeId
            and exists (
                select 1 from Reservation r
                where r.detail.id = d.id
                and r.status = 'RESERVED'
            )
            """)
    List<ReservationTime> findAllReservedTimeByDateAndThemeId(
            @Param("date") LocalDate date,
            @Param("themeId") Long themeId
    );

    @Query("""
            select d.time from ReservationDetail d
            where d.date = :date
            and d.theme.id = :themeId
            and exists (
                select 1 from Reservation r
                where r.detail.id = d.id
                and r.status = 'RESERVED' or r.status = 'PAYMENT_PENDING'
            )
            """)
    List<ReservationTime> findAllUnAvailableTimes(
            @Param("date") LocalDate date,
            @Param("themeId") Long themeId
    );

    boolean existsByStartAt(LocalTime startAt);

    void delete(ReservationTime time);
}