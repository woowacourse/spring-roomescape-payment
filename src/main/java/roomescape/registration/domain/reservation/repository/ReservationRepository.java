package roomescape.registration.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.reservation.dto.ReservationResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByOrderByDateAscReservationTimeAsc();

    List<Reservation> findAllByThemeIdAndDate(long themeId, LocalDate date);

    List<Reservation> findAllByMemberIdAndThemeIdAndDateBetween(long memberId, long themeId, LocalDate fromDate, LocalDate toDate);

    Reservation findReservationByDateAndThemeIdAndReservationTimeId(LocalDate date, long themeId, long reservationTimeId);

    List<Reservation> findByReservationTimeId(long timeId);

    List<Reservation> findByThemeId(long themeId);

    void deleteById(long reservationId);

    List<Reservation> findAllByMemberId(long id);

    boolean existsByDateAndThemeIdAndReservationTimeIdAndMemberId(LocalDate date, long themeId, long reservationTimeId, long memberId);

    @Query("SELECT new roomescape.registration.domain.reservation.dto.ReservationResponse(" +
            "r.id, " +
            "m.name.name, " +
            "t.name.name, " +
            "r.date, " +
            "r.reservationTime.startAt, " +
            "new roomescape.registration.dto.PaymentResponse(p.id, p.createdAt, p.reservation.theme.price)) " +
            "FROM Payment p " +
            "RIGHT JOIN p.reservation r " +
            "JOIN r.member m " +
            "JOIN r.theme t " +
            "WHERE m.id = :memberId")
    List<ReservationResponse> findAllReservationsWithPaymentsByMemberId(@Param("memberId") long memberId);
}
