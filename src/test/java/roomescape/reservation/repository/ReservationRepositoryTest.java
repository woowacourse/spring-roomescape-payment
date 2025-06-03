package roomescape.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import roomescape.config.TestConfig;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.infrastructure.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.fixture.TestFixture;
import roomescape.reservation.repository.dto.ReservationWithPayment;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.response.AvailableReservationTimeResponse;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@DataJpaTest
@Import(TestConfig.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
class ReservationRepositoryTest {

    private static final LocalDate futureDate = TestFixture.makeFutureDate();

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    private ReservationTime reservationTime;

    private Theme theme;

    @BeforeEach
    public void setup() {
        member = memberRepository.save(TestFixture.makeMember());
        reservationTime = reservationTimeRepository.save(ReservationTime.withUnassignedId(LocalTime.of(10, 0)));
        theme = themeRepository.save(TestFixture.makeTheme(1L));
        reservationRepository.save(
                new Reservation(member, new ReservationInfo(futureDate, reservationTime, theme)));
    }

    @Test
    void findByInfoThemeIdAndMemberIdAndInfoDateBetween_shouldReturnFilteredReservations() {
        Theme theme2 = themeRepository.save(Theme.of("논리", "셜록 논리 게임 with Vector", "image.png"));

        ReservationTime reservationTime2 = ReservationTime.withUnassignedId(LocalTime.of(11, 0));
        reservationTime2 = reservationTimeRepository.save(reservationTime2);

        Reservation reservation2 = new Reservation(member, new ReservationInfo(futureDate, reservationTime2, theme2));
        reservationRepository.save(reservation2);

        List<Reservation> filteredReservations = reservationRepository.findByInfoThemeIdAndMemberIdAndInfoDateBetween(
                theme.getId(),
                member.getId(), futureDate,
                futureDate.plusDays(1));

        assertThat(filteredReservations.size()).isEqualTo(1);
    }

    @Test
    void existsByTimeId_shouldReturnTrue() {
        boolean existsByTimeId = reservationRepository.existsByTimeId(reservationTime.getId());

        assertThat(existsByTimeId).isTrue();
    }

    @Test
    void existsByThemeId_shouldReturnTrue() {
        boolean existsByThemeId = reservationRepository.existsByThemeId(theme.getId());

        assertThat(existsByThemeId).isTrue();
    }

    @Test
    void existsByInfoDateAndInfoTimeIdAndInfoThemeId_shouldReturnTrue() {
        boolean existsByDateAndTimeIdAndThemeId = reservationRepository.existsByInfoDateAndInfoTimeIdAndInfoThemeId(
                futureDate,
                reservationTime.getId(),
                theme.getId());

        assertThat(existsByDateAndTimeIdAndThemeId).isTrue();
    }

    @Test
    void findBookedTimesByDateAndThemeId_shouldReturnBookedTimes() {
        ReservationTime reservationTime2 = reservationTimeRepository.save(
                ReservationTime.withUnassignedId(LocalTime.of(11, 0)));
        ReservationTime reservationTime3 = reservationTimeRepository.save(
                ReservationTime.withUnassignedId(LocalTime.of(12, 0)));

        reservationRepository.save(
                new Reservation(member, new ReservationInfo(futureDate, reservationTime2, theme)));
        reservationRepository.save(
                new Reservation(member, new ReservationInfo(futureDate, reservationTime3, theme)));

        List<AvailableReservationTimeResponse> bookedTimesByDateAndThemeId = reservationRepository.findBookedTimesByDateAndThemeId(
                futureDate, theme.getId());

        assertThat(bookedTimesByDateAndThemeId.size()).isEqualTo(3);
    }

    @Test
    void findReservationWithPaymentByMemberId_test() {
        ReservationTime reservationTime2 = reservationTimeRepository.save(
                ReservationTime.withUnassignedId(LocalTime.of(11, 0)));
        ReservationTime reservationTime3 = reservationTimeRepository.save(
                ReservationTime.withUnassignedId(LocalTime.of(12, 0)));
        Member newMember = new Member("cogi", "cogi@gmail.com", "password", MemberRole.USER);
        memberRepository.save(newMember);
        Reservation reservation = new Reservation(member, new ReservationInfo(futureDate, reservationTime2, theme));
        reservationRepository.save(reservation);
        reservationRepository.save(
                new Reservation(newMember, new ReservationInfo(futureDate, reservationTime3, theme)));
        Payment payment = Payment.createPaymentWithoutId("a", "a", 1000, LocalDateTime.now(), reservation);
        paymentRepository.save(payment);

        List<ReservationWithPayment> reservationWithPayments = reservationRepository.findReservationWithPaymentByMemberId(
                member.getId());

        assertThat(reservationWithPayments).hasSize(2);
        assertThat(reservationWithPayments.get(0).getPayment()).isEmpty();
        assertThat(reservationWithPayments.get(1).getPayment()).isPresent();
    }

    @Test
    void findReservationWithPaymentByMemberId_test2() {
        ReservationTime reservationTime2 = reservationTimeRepository.save(
                ReservationTime.withUnassignedId(LocalTime.of(11, 0)));
        ReservationTime reservationTime3 = reservationTimeRepository.save(
                ReservationTime.withUnassignedId(LocalTime.of(12, 0)));
        Member newMember = new Member("cogi", "cogi@gmail.com", "password", MemberRole.USER);
        memberRepository.save(newMember);
        Reservation reservation = new Reservation(newMember, new ReservationInfo(futureDate, reservationTime2, theme));
        reservationRepository.save(reservation);
        reservationRepository.save(
                new Reservation(member, new ReservationInfo(futureDate, reservationTime3, theme)));
        Payment payment = Payment.createPaymentWithoutId("a", "a", 1000, LocalDateTime.now(), reservation);
        paymentRepository.save(payment);

        List<ReservationWithPayment> reservationWithPayments = reservationRepository.findReservationWithPaymentByMemberId(
                newMember.getId());

        assertThat(reservationWithPayments).hasSize(1);
        assertThat(reservationWithPayments.get(0).getPayment()).isPresent();
        assertThat(reservationWithPayments.get(0).getReservation().getMember().getName()).isEqualTo("cogi");
    }
}
