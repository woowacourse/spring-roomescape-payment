package roomescape.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.Fixture;
import roomescape.auth.TokenProvider;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationWaiting;
import roomescape.domain.reservation.ReservationWaitingRepository;
import roomescape.domain.schedule.ReservationDate;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.ReservationTimeRepository;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.InvalidReservationException;
import roomescape.exception.UnauthorizedException;
import roomescape.service.ServiceTestBase;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationWaitingResponse;

class ReservationWaitingServiceTest extends ServiceTestBase {
    @Autowired
    private ReservationWaitingService reservationWaitingService;
    @Autowired
    private ReservationWaitingRepository reservationWaitingRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TokenProvider tokenProvider;
    private ReservationTime reservationTime;
    private Theme theme;
    private Member member;
    private String token;
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        reservationTime = reservationTimeRepository.save(Fixture.reservationTime);
        theme = themeRepository.save(Fixture.theme);
        member = memberRepository.save(Fixture.member);
        token = tokenProvider.create(member);
    }

    @DisplayName("새로운 예약 대기를 저장한다.")
    @Test
    void create() {
        // given
        LocalDate date = Fixture.tomorrow;
        ReservationRequest request = new ReservationRequest(
                date, reservationTime.getId(), theme.getId(), "invalid paymentkey", "orderId", 1000L, "pay type"
        );
        saveReservationOfDate(date);

        // when
        ReservationWaitingResponse response = reservationWaitingService.create(request, member.getId());

        // then
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(response.id()).isPositive();
        assertions.assertThat(response.time().id()).isEqualTo(reservationTime.getId());
        assertions.assertThat(response.theme().id()).isEqualTo(theme.getId());
        assertions.assertThat(response.theme().id()).isEqualTo(theme.getId());
        assertions.assertThat(response.createdAt()).isBefore(LocalDateTime.now());
        assertions.assertAll();
    }

    @DisplayName("예약 대기를 중복으로 등록하는 경우 예외가 발생한다.")
    @Test
    void throwsExceptionOnDuplicatedWaiting() {
        // given
        LocalDate date = Fixture.tomorrow;
        ReservationRequest request = new ReservationRequest(
                date, reservationTime.getId(), theme.getId(), "invalid paymentkey", "orderId", 1000L, "pay type"
        );
        saveReservationOfDate(date);
        reservationWaitingService.create(request, member.getId());

        // when & then
        long memberId = member.getId();
        assertThatThrownBy(() -> reservationWaitingService.create(request, memberId))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("이미 해당 테마에 예약 대기를 등록했습니다.");
    }

    private void saveReservationOfDate(LocalDate date) {
        Reservation reservation = new Reservation(
                member, new Schedule(ReservationDate.of(date), reservationTime), theme, ReservationStatus.RESERVED
        );
        reservationRepository.save(reservation);
    }

    @DisplayName("id로 등록된 예약 대기를 취소한다.")
    @Test
    void deleteById() {
        // given
        Schedule schedule = new Schedule(ReservationDate.of(Fixture.tomorrow), reservationTime);
        Payment payment = paymentRepository.save(new Payment("ADMIN", "ADMIN", 0));
        ReservationWaiting waiting = new ReservationWaiting(member, theme, schedule, payment);
        ReservationWaiting target = reservationWaitingRepository.save(waiting);

        // when
        reservationWaitingService.deleteById(target.getId(), tokenProvider.extractMemberId(token));

        // then
        assertThat(reservationWaitingRepository.findById(target.getId())).isNotPresent();
    }

    @DisplayName("회원 정보가 일치하지 않으면 예약 취소 시 에외가 발생한다.")
    @Test
    void throwsExceptionWhenMemberIdNotMatched() {
        // given
        Schedule schedule = new Schedule(ReservationDate.of(Fixture.tomorrow), reservationTime);
        ReservationWaiting waiting = new ReservationWaiting(member, theme, schedule);
        ReservationWaiting target = reservationWaitingRepository.save(waiting);

        // when & then
        Long targetId = target.getId();
        assertThatThrownBy(() -> reservationWaitingService.deleteById(targetId, -1))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("예약 대기를 취소할 권한이 없습니다.");
    }
}
