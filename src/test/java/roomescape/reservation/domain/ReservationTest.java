package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.exception.ReservationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.repository.PaymentRepository;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@DataJpaTest
class ReservationTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    private Member member;
    private Theme theme;
    private ReservationTime time;
    private Payment payment;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.withDefaultRole("홍길동", "hong@example.com", "password"));
        theme = themeRepository.save(Theme.of("테마명", "테마 설명", "thumbnail.jpg"));
        time = reservationTimeRepository.save(ReservationTime.from(LocalTime.of(13, 0)));
        payment = paymentRepository.save(Payment.from("order_id_test_01", "payment_key_hong", 1000L));
    }

    private Clock clock = Clock.systemDefaultZone();
    private Theme defaultTheme = Theme.of("테마", "설명", "썸네일");
    private Member defaultMember = Member.withRole("member", "member@naver.com", "1234", MemberRole.MEMBER);
    private Payment defaultPayment = Payment.from("test_order_id", "test_payment_key", 1000L);

    @Test
    void 새_예약의_id_필드는_null이다() {
        // given
        LocalDate today = LocalDate.now();
        LocalTime later = LocalTime.now().plusMinutes(5);
        ReservationTime rt = ReservationTime.from(later);
        Reservation reservation = Reservation.of(
                today, rt, defaultTheme, defaultMember, LocalDateTime.now(clock), defaultPayment
        );
        // when
        // then
        assertThat(reservation.getId()).isNull();
    }

    @Test
    void id_필드를_제외한_필드가_null이면_예외처리() {
        // given
        LocalDate localDate = LocalDate.of(2999, 1, 1);
        LocalDateTime currentDateTime = LocalDateTime.now(clock);
        ReservationTime reservationTime = ReservationTime.from(LocalTime.of(11, 0));
        Theme theme = Theme.of("test", "test", "test");
        Member member = Member.withRole("member", "member@naver.com", "1234", MemberRole.MEMBER);
        Payment paymentInfo = Payment.from("order_01", "key_01", 10L);
        // when
        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThatThrownBy(
                            () -> Reservation.of(null, reservationTime, theme, member, currentDateTime, paymentInfo))
                    .isInstanceOf(NullPointerException.class);
            softly.assertThatThrownBy(
                            () -> Reservation.of(localDate, null, theme, member, currentDateTime, paymentInfo))
                    .isInstanceOf(NullPointerException.class);
            softly.assertThatThrownBy(
                            () -> Reservation.of(localDate, reservationTime, null, member, currentDateTime, paymentInfo))
                    .isInstanceOf(NullPointerException.class);
            softly.assertThatThrownBy(
                            () -> Reservation.of(localDate, reservationTime, theme, null, currentDateTime, paymentInfo))
                    .isInstanceOf(NullPointerException.class);
            softly.assertThatThrownBy(
                            () -> Reservation.of(localDate, reservationTime, theme, member, null, paymentInfo))
                    .isInstanceOf(NullPointerException.class);
        });
    }

    @Test
    void 예약_생성_성공() {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        LocalDateTime currentDateTime = LocalDateTime.now();

        // when
        Reservation reservation = Reservation.of(date, time, theme, member, currentDateTime, payment);
        Reservation savedReservation = reservationRepository.save(reservation);

        // then
        assertThat(savedReservation.getId()).isNotNull();
        assertThat(savedReservation.getDate()).isEqualTo(date);
        assertThat(savedReservation.getTime()).isEqualTo(time);
        assertThat(savedReservation.getTheme()).isEqualTo(theme);
        assertThat(savedReservation.getMember()).isEqualTo(member);
        assertThat(savedReservation.getReservationStatus().getStatus()).isEqualTo(Status.BOOKED);
    }

    @Test
    void 예약_시간_검증_실패() {
        // given
        LocalDate date = LocalDate.now().minusDays(1);
        LocalDateTime currentDateTime = LocalDateTime.now();

        // when & then
        assertThatThrownBy(() ->
                Reservation.of(date, time, theme, member, currentDateTime, defaultPayment)
        )
                .isInstanceOf(ReservationException.class)
                .hasMessage("예약은 현재 시간 이후로 가능합니다.");
    }

    @Test
    void 대기_목록_생성_성공() {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        LocalDateTime currentDateTime = LocalDateTime.now();
        Long rank = 1L;

        // when
        Reservation waitingReservation = Reservation.waiting(date, time, theme, member, currentDateTime, rank);
        Reservation savedReservation = reservationRepository.save(waitingReservation);

        // then
        assertThat(savedReservation.getReservationStatus().getStatus()).isEqualTo(Status.WAITING);
        assertThat(savedReservation.getReservationStatus().getRank()).isEqualTo(rank);
    }
}
