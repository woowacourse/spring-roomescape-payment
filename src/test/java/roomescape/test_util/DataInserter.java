package roomescape.test_util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRole;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.repository.jpa.MemberJpaRepository;
import roomescape.repository.jpa.PaymentJpaRepository;
import roomescape.repository.jpa.ReservationItemJpaRepository;
import roomescape.repository.jpa.ReservationJpaRepository;
import roomescape.repository.jpa.ReservationThemeJpaRepository;
import roomescape.repository.jpa.ReservationTimeJpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

@RequiredArgsConstructor
@TestComponent
public class DataInserter {

    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationItemJpaRepository reservationItemJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final ReservationTimeJpaRepository reservationTimeJpaRepository;
    private final ReservationThemeJpaRepository reservationThemeJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;

    public Payment insertPayment(String paymentKey, int amount, Reservation reservation) {
        Payment payment = new Payment(paymentKey, amount, reservation);
        return paymentJpaRepository.save(payment);
    }

    public Reservation insertReservation(Member member, ReservationItem reservationItem, ReservationStatus reservationStatus) {
        Reservation reservation = Reservation.builder()
                .member(member)
                .reservationItem(reservationItem)
                .reservationStatus(reservationStatus)
                .build();
        return reservationJpaRepository.save(reservation);
    }

    public Member insertMember(String email, String password, String name, MemberRole role) {
        Member member = Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .role(role)
                .build();
        return memberJpaRepository.save(member);
    }

    public ReservationTime insertReservationTime(LocalTime startAt) {
        ReservationTime time = new ReservationTime(startAt);
        return reservationTimeJpaRepository.save(time);
    }

    public ReservationTheme insertReservationTheme(String name, String description, String thumbnail) {
        ReservationTheme theme = ReservationTheme.builder()
                .name(name)
                .description(description)
                .thumbnail(thumbnail)
                .build();
        return reservationThemeJpaRepository.save(theme);
    }

    public ReservationItem insertReservationItem(LocalDate date, ReservationTime time, ReservationTheme theme) {
        ReservationItem reservationItem = ReservationItem.builder()
                .date(date)
                .time(time)
                .theme(theme)
                .build();
        return reservationItemJpaRepository.save(reservationItem);
    }
}
