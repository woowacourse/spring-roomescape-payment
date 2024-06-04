package roomescape.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationdetail.ReservationTime;

@Repository
public interface ReservationTimeJpaRepository extends JpaRepository<ReservationTime, Long> {

    ReservationTime save(ReservationTime time);

    Optional<ReservationTime> findById(Long id);

    List<ReservationTime> findAll();

    @Query(value = """
            select
            t.id as reservation_time_id,
            t.start_at as time_value
            from reservation as r
            inner join reservation_time as t on r.reservation_time_id = t.id
            inner join theme as th on r.theme_id = th.id
            where r.date = :date
            and r.theme_id = :themeId
            """, nativeQuery = true)
    List<ReservationTime> findAllReservedTimeByDateAndThemeId(
            @Param("date") LocalDate date,
            @Param("themeId") Long themeId
    );

    boolean existsByStartAt(LocalTime startAt);

    void deleteById(Long id);
}
