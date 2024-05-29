package roomescape.domain.reservation;

import java.util.List;
import java.util.Optional;
import roomescape.domain.dto.ReservationWithRank;
import roomescape.domain.schedule.ReservationDate;

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
