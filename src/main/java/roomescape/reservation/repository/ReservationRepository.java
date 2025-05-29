package roomescape.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.reservation.domain.Reservation;
import roomescape.waiting.domain.ReservationInformation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r JOIN FETCH r.member JOIN FETCH r.reservationInformation.theme JOIN FETCH r.reservationInformation.time")
    List<Reservation> findAll();

    @Query("SELECT r FROM Reservation r JOIN FETCH r.member m JOIN FETCH r.reservationInformation.theme JOIN FETCH r.reservationInformation.time WHERE m.id = :memberId")
    List<Reservation> findAllByMemberId(Long memberId);

    Optional<Reservation> findById(Long id);

    Optional<Reservation> findByIdAndMemberId(Long id, Long memberId);

    @Query("""
            SELECT r FROM Reservation r
            WHERE (:memberId IS NULL OR r.member.id = :memberId)
                AND (:themeId IS NULL OR r.reservationInformation.theme.id = :themeId)
                AND (:dateFrom IS NULL OR r.reservationInformation.date >= :dateFrom)
                AND (:dateTo IS NULL OR r.reservationInformation.date <= :dateTo)
            """)
    List<Reservation> findByThemeIdAndMemberIdAndDateBetween(
            Long themeId,
            Long memberId,
            LocalDate dateFrom,
            LocalDate dateTo
    );

    boolean existsByReservationInformation(ReservationInformation reservationInformation);

    @Query("""
    SELECT EXISTS (
        SELECT 1 FROM Reservation r
        WHERE r.reservationInformation.time.id = :timeId
    )
    """)
    boolean existsByTimeId(final Long timeId);

    @Query("""
    SELECT EXISTS (
        SELECT 1 FROM Reservation r
        WHERE r.reservationInformation.theme.id = :themeId
      )
    """)
    boolean existsByThemeId(final Long themeId);
}
