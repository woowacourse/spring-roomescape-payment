package roomescape.registration.domain.waiting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import roomescape.registration.domain.waiting.domain.Waiting;
import roomescape.registration.domain.waiting.domain.WaitingWithRank;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WaitingRepository extends CrudRepository<Waiting, Long> {

    List<Waiting> findAll();

    Optional<Waiting> findByReservationMemberId(Long id);

    @Query("SELECT new roomescape.registration.domain.waiting.domain.WaitingWithRank(" +
            "    w, " +
            "    (SELECT COUNT(w2) + 1" +
            "     FROM Waiting w2 " +
            "     WHERE w2.reservation.theme.name = w.reservation.theme.name " +
            "       AND w2.reservation.date = w.reservation.date " +
            "       AND w2.reservation.reservationTime.startAt = w.reservation.reservationTime.startAt " +
            "       AND w2.id < w.id))" +
            "FROM Waiting w " +
            "WHERE w.reservation.member.id = :memberId")
    List<WaitingWithRank> findWaitingsWithRankByMemberId(long memberId);

    Waiting findByReservationDateAndReservationThemeIdAndReservationReservationTimeIdAndReservationMemberId(
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

    boolean existsByReservationDateAndReservationThemeIdAndReservationReservationTimeIdAndReservationMemberId(
            LocalDate date,
            long themeId,
            long reservationTimeId,
            long memberId
    );
}
