package roomescape.reservation.infrastructure;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationWithPayment;

public interface ReservationJpaRepository extends CrudRepository<Reservation, Long> {

    boolean existsByTimeId(Long timeId);

    boolean existsByThemeId(Long themeId);

    List<Reservation> findAll();

    List<Reservation> findAllByDateAndThemeId(LocalDate date, Long themeId);

    List<Reservation> findAllByMemberIdAndThemeIdAndDateBetween(Long memberId, Long themeId, LocalDate from,
                                                                LocalDate to);

    List<Reservation> findAllByThemeIdAndDate(Long themeId, LocalDate date);

    @Query("""
            SELECT new roomescape.reservation.dto.ReservationWithPayment(r, p)
            FROM Reservation r
            INNER JOIN Payment p
                ON p.reservation.id = r.id
            WHERE r.member.id = :memberId
            """)
    List<ReservationWithPayment> findAllWithPaymentByMemberId(@Param("memberId") Long memberId);

    @Query("""
            SELECT r
            FROM Reservation r
            LEFT JOIN Payment p
                ON p.reservation.id = r.id
            WHERE r.member.id = :memberId AND p.id is null
            """)
    List<Reservation> findAllWithoutPaymentByMemberId(@Param("memberId") Long memberId);

    Optional<Reservation> findByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId);
}
