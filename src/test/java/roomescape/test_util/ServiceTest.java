package roomescape.test_util;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRole;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.repository.jpa.ReservationJpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@Import({DataCleaner.class, DataInserter.class})
public abstract class ServiceTest {

    @Autowired
    private DataCleaner dataCleaner;
    @Autowired
    private DataInserter dataInserter;
    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @AfterEach
    void tearDown() {
        dataCleaner.clean();
    }

    public Payment insertPayment(String paymentKey, int amount, Reservation reservation) {
        return dataInserter.insertPayment(paymentKey, amount, reservation);
    }

    public Reservation insertReservation(Member member, ReservationItem reservationItem, ReservationStatus reservationStatus) {
        return dataInserter.insertReservation(member, reservationItem, reservationStatus);
    }

    public Member insertMember(String email, String password, String name, MemberRole role) {
        return dataInserter.insertMember(email, password, name, role);
    }

    public ReservationTime insertReservationTime(LocalTime startAt) {
        return dataInserter.insertReservationTime(startAt);
    }

    public ReservationTheme insertReservationTheme(String name, String description, String thumbnail) {
        return dataInserter.insertReservationTheme(name, description, thumbnail);
    }

    public ReservationItem insertReservationItem(LocalDate date, ReservationTime time, ReservationTheme theme) {
        return dataInserter.insertReservationItem(date, time, theme);
    }

    public List<Reservation> getAllReservations() {
        return reservationJpaRepository.findAll();
    }

    public int countReservation() {
        return (int) reservationJpaRepository.count();
    }
}
