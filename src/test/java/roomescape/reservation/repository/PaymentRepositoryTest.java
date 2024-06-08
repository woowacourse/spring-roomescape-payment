package roomescape.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;

@DataJpaTest
class PaymentRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PaymentRepository paymentRepository;


    @Test
    @DisplayName("예약을 바탕으로 결제 정보 영속성을 조회한다.")
    void existsPaymentByReservationIdShouldCheckExistenceByReservationId() {
        // given
        Member member = new Member("name", "aa@aaa.a", "aa");
        ReservationTime time = new ReservationTime(LocalTime.of(1, 0));
        Theme theme = new Theme("a", "a", "A");
        Reservation reservation = new Reservation(member, LocalDate.now().plusDays(1), theme, time, Status.SUCCESS);
        Payment payment = new Payment(1000L, "paymentKey", reservation);

        memberRepository.save(member);
        reservationTimeRepository.save(time);
        themeRepository.save(theme);
        reservationRepository.save(reservation);
        paymentRepository.save(payment);

        // when
        boolean result = paymentRepository.existsByReservation(reservation);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("사용자 아이디를 바탕으로 예약을 가져온다.")
    void findAllByReservationMemberIdShouldGetPersistence() {
        // given
        Member member = new Member("name", "aa@aaa.a", "aa");
        ReservationTime time = new ReservationTime(LocalTime.of(1, 0));
        Theme theme = new Theme("a", "a", "A");
        Reservation reservation = new Reservation(member, LocalDate.now().plusDays(1), theme, time, Status.SUCCESS);
        Payment payment = new Payment(1000L, "paymentKey", reservation);

        memberRepository.save(member);
        reservationTimeRepository.save(time);
        themeRepository.save(theme);
        reservationRepository.save(reservation);
        paymentRepository.save(payment);

        // when
        List<Payment> payments = paymentRepository.findByReservationMemberId(member.getId());

        // then
        assertThat(payments).containsExactly(payment);

    }
}
