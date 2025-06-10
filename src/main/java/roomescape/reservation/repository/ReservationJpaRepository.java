package roomescape.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import roomescape.reservation.domain.Reservation;

@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {
    boolean existsByDateAndTimeIdAndThemeId(LocalDate date, long timeId, long themeId);

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.theme " +
            "JOIN FETCH r.time " +
            "JOIN FETCH r.member " +
            "WHERE r.member.id = :memberId")
    List<Reservation> findByMemberId(@Param("memberId") Long memberId);

    boolean existsByThemeId(long themeId);

    boolean existsByTimeId(long timeId);

    boolean existsByMemberIdAndDateAndThemeIdAndTimeId(final Long memberId, final LocalDate date, final long themeId,
                                                       final long timeId);
}
