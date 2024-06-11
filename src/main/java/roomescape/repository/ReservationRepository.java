package roomescape.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import roomescape.model.*;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    String test = "SELECT r.theme FROM Reservation";

    List<Reservation> findAll();

    Reservation save(Reservation reservation);

    @Query("""
            SELECT r FROM Reservation r
            WHERE (:theme IS NULL OR r.theme = :theme)
            AND (:member IS NULL OR r.member = :member)
            AND (:dateFrom IS NULL OR r.date >= :dateFrom)
            AND (:dateTo IS NULL OR r.date <= :dateTo)
            """)
    List<Reservation> findByConditions(Theme theme, Member member, LocalDate dateFrom,
                                       LocalDate dateTo);

    void deleteById(long id);

    boolean existsById(long id);

    boolean existsByTime(ReservationTime time);

    boolean existsByDateAndTimeAndTheme(LocalDate date, ReservationTime time, Theme theme);

    List<Reservation> findAllByDateAndTheme(LocalDate date, Theme theme);

    List<Reservation> findAllByMember(Member member);

    @Query("""
            SELECT new roomescape.model.ReservationWithPaymentInfo(
            r, p)
            FROM Reservation r
            INNER JOIN PaymentInfo p
            ON r.id = p.reservation.id
            WHERE r.member = :member
            """)
    List<ReservationWithPaymentInfo> findAllByMemberWithPaymentInfo(Member member);

    @Query("""
            SELECT r
            FROM Reservation r
            LEFT OUTER JOIN PaymentInfo p
            ON r.id = p.reservation.id
            WHERE r.member = :member
            AND p.id IS NULL
            """)
    List<Reservation> findAllByMemberWithoutPaymentInfo(Member member);

    boolean existsReservationByThemeAndDateAndTimeAndMember(Theme theme, LocalDate date, ReservationTime time, Member member);

}
