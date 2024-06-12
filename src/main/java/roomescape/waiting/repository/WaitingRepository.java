package roomescape.waiting.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingWithRank;

@Tag(name = "예약 레포지토리", description = "예약 대기 DB를 활용해 대기 순서, 대기 삭제 등을 수행한다.")
public interface WaitingRepository extends CrudRepository<Waiting, Long> {

    List<Waiting> findAll();

    @Query("SELECT new roomescape.registration.domain.waiting.domain.WaitingWithRank(" +
            "    w, " +
            "    (SELECT COUNT(w2) + 1" +
            "     FROM Waiting w2 " +
            "     WHERE w2.reservation.theme.name = w.reservation.theme.name " +
            "       AND w2.reservation.date = w.reservation.date " +
            "       AND w2.reservation.reservationTime.startAt = w.reservation.reservationTime.startAt " +
            "       AND w2.id < w.id))" +
            "FROM Waiting w " +
            "WHERE w.member.id = :memberId")
    List<WaitingWithRank> findWaitingsWithRankByMemberId(long memberId);

    Waiting findByReservationDateAndReservationThemeIdAndReservationReservationTimeIdAndMemberId(
            LocalDate date,
            long themeId,
            long reservationTimeId,
            long memberId
    );

    Optional<Waiting> findFirstByReservationIdOrderByCreatedAt(long reservationId);

    @Query("SELECT COUNT(w) "
            + "FROM Waiting w "
            + "WHERE w.reservation.date = :date "
            + "AND w.reservation.theme.id = :themeId "
            + "AND w.reservation.reservationTime.id = :reservationTimeId "
            + "AND w.id < :id")
    long countWaitingRankByDateAndThemeIdAndReservationTimeId(
            long id,
            LocalDate date,
            long themeId,
            long reservationTimeId
    );

    void deleteById(long id);

    boolean existsByReservationDateAndReservationThemeIdAndReservationReservationTimeIdAndMemberId(
            LocalDate date,
            long themeId,
            long reservationTimeId,
            long memberId
    );
}
