package roomescape.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.domain.dto.ReservationWithRank;
import roomescape.domain.schedule.ReservationDate;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    List<Reservation> findBy(Long memberId, Long themeId,
                             ReservationDate dateFrom, ReservationDate dateTo);

    boolean existsByDetailIdAndMemberId(Long reservationDetailId, Long memberId);

    boolean existsByDetailIdAndStatus(Long reservationDetailId, ReservationStatus status);

    boolean existsByDetailThemeId(long themeId);

    boolean existsByDetailScheduleTimeId(long timeId);

    Optional<Reservation> findFirstByDetailIdOrderByCreatedAt(long detailId);

    List<Reservation> findAllByStatus(ReservationStatus status);

    List<ReservationWithRank> findWithRankingByMemberId(long memberId);

    Optional<Reservation> findById(long id);

    void deleteById(long id);
}
