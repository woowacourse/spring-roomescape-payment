package roomescape.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select r from Reservation r
            join fetch r.roomEscapeInformation re
            where (:themeId is null or re.theme.id = :themeId)
              and (:memberId is null or r.member.id = :memberId)
              and (:localDateFrom is null or re.date >= :localDateFrom)
              and (:localDateTo is null or re.date <= :localDateTo)
            """)
    List<Reservation> findByCriteria(
            @Param("themeId") final Long themeId,
            @Param("memberId") final Long memberId,
            @Param("localDateFrom") final LocalDate localDateFrom,
            @Param("localDateTo") final LocalDate localDateTo
    );

    List<Reservation> findByMember(final Member member);

    boolean existsByRoomEscapeInformationId(Long roomEscapeInformationId);

    boolean existsByRoomEscapeInformationDateAndRoomEscapeInformationTimeAndRoomEscapeInformationThemeAndStatus(
            LocalDate date,
            ReservationTime time,
            Theme theme,
            ReservationStatus status);
}
