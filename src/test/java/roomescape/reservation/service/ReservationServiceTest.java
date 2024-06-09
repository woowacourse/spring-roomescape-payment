package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.member.service.MemberService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.WaitingRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.system.exception.RoomEscapeException;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.repository.ThemeRepository;
import roomescape.theme.service.ThemeService;

@SpringBootTest
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Import({ReservationService.class, MemberService.class, ReservationTimeService.class, ThemeService.class})
class ReservationServiceTest {

    @Autowired
    ReservationTimeRepository reservationTimeRepository;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    ThemeRepository themeRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private ReservationService reservationService;

    @Test
    @DisplayName("예약을 추가할때 이미 예약이 존재하면 예외가 발생한다.")
    void reservationAlreadyExistFail() {
        // given
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(12, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member member1 = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));
        Member member2 = memberRepository.save(new Member("name2", "email2@email.com", "password", Role.MEMBER));
        LocalDate date = LocalDate.now().plusDays(1L);

        // when
        reservationService.addReservation(
                new ReservationRequest(date, reservationTime.getId(), theme.getId(), "paymentKey", "orderId",
                        1000L, "paymentType"), member2.getId());

        // then
        assertThatThrownBy(() -> reservationService.addReservation(
                new ReservationRequest(date, reservationTime.getId(), theme.getId(), "paymentKey", "orderId",
                        1000L, "paymentType"), member1.getId()))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    @DisplayName("이미 예약한 멤버가 같은 테마에 대기를 신청하면 예외가 발생한다.")
    void requestWaitWhenAlreadyReserveFail() {
        // given
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(12, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));
        LocalDate date = LocalDate.now().plusDays(1L);

        // when
        reservationService.addReservation(
                new ReservationRequest(date, reservationTime.getId(), theme.getId(), "paymentKey", "orderId",
                        1000L, "paymentType"), member.getId());

        // then
        assertThatThrownBy(() -> reservationService.addWaiting(
                new WaitingRequest(date, reservationTime.getId(), theme.getId()), member.getId()))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    @DisplayName("예약 대기를 두 번 이상 요청하면 예외가 발생한다.")
    void requestWaitTwiceFail() {
        // given
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(12, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));
        Member member1 = memberRepository.save(new Member("name1", "email1@email.com", "password", Role.MEMBER));
        LocalDate date = LocalDate.now().plusDays(1L);

        // when
        reservationService.addReservation(
                new ReservationRequest(date, reservationTime.getId(), theme.getId(), "paymentKey", "orderId",
                        1000L, "paymentType"), member.getId());

        reservationService.addWaiting(
                new WaitingRequest(date, reservationTime.getId(), theme.getId()), member1.getId());

        // then
        assertThatThrownBy(() -> reservationService.addWaiting(
                new WaitingRequest(date, reservationTime.getId(), theme.getId()), member1.getId()))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    @DisplayName("이미 지난 날짜로 예약을 생성하면 예외가 발생한다.")
    void beforeDateReservationFail() {
        // given
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(12, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));
        LocalDate beforeDate = LocalDate.now().minusDays(1L);

        // when & then
        assertThatThrownBy(() -> reservationService.addReservation(
                new ReservationRequest(beforeDate, reservationTime.getId(), theme.getId(), "paymentKey", "orderId",
                        1000L, "paymentType"), member.getId()))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    @DisplayName("현재 날짜가 예약 당일이지만, 이미 지난 시간으로 예약을 생성하면 예외가 발생한다.")
    void beforeTimeReservationFail() {
        // given
        LocalDateTime beforeTime = LocalDateTime.now().minusHours(1L);
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(beforeTime.toLocalTime()));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));

        // when & then
        assertThatThrownBy(() -> reservationService.addReservation(
                new ReservationRequest(beforeTime.toLocalDate(), reservationTime.getId(), theme.getId(), "paymentKey",
                        "orderId", 1000L, "paymentType"), member.getId()))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    @DisplayName("존재하지 않는 회원이 예약을 생성하려고 하면 예외가 발생한다.")
    void notExistMemberReservationFail() {
        // given
        LocalDateTime beforeTime = LocalDateTime.now().minusHours(1L);
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(beforeTime.toLocalTime()));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Long NotExistMemberId = 1L;

        // when & then
        assertThatThrownBy(() -> reservationService.addReservation(
                new ReservationRequest(beforeTime.toLocalDate(), reservationTime.getId(), theme.getId(), "paymentKey",
                        "orderId", 1000L, "paymentType"),
                NotExistMemberId))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    @DisplayName("예약을 조회할 때 종료 날짜가 시작 날짜 이전이면 예외가 발생한다.")
    void invalidDateRange() {
        // given
        LocalDate dateFrom = LocalDate.now().plusDays(1);
        LocalDate dateTo = LocalDate.now();

        // when & then
        assertThatThrownBy(() -> reservationService.findFilteredReservations(null, null, dateFrom, dateTo))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    @DisplayName("대기중인 예약을 승인할 때, 기존에 예약이 존재하면 예외가 발생한다.")
    void confirmWaitingWhenReservationExist() {
        // given
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(12, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member admin = memberRepository.save(new Member("admin", "admin@email.com", "password", Role.ADMIN));
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));
        Member member1 = memberRepository.save(new Member("name1", "email1@email.com", "password", Role.MEMBER));

        reservationService.addReservation(
                new ReservationRequest(LocalDate.now().plusDays(1L), reservationTime.getId(), theme.getId(),
                        "paymentKey", "orderId",
                        1000L, "paymentType"), member.getId());
        ReservationResponse waiting = reservationService.addWaiting(
                new WaitingRequest(LocalDate.now().plusDays(1L), reservationTime.getId(), theme.getId()),
                member1.getId());

        // when & then
        assertThatThrownBy(() -> reservationService.approveWaiting(waiting.id(), admin.getId()))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    @DisplayName("대기중인 예약을 확정한다.")
    void approveWaiting() {
        // given
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(12, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member admin = memberRepository.save(new Member("admin", "admin@email.com", "password", Role.ADMIN));
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));

        // when
        ReservationResponse waiting = reservationService.addWaiting(
                new WaitingRequest(LocalDate.now().plusDays(1L), reservationTime.getId(), theme.getId()),
                member.getId());
        reservationService.approveWaiting(waiting.id(), admin.getId());

        // then
        Reservation confirmed = reservationRepository.findById(waiting.id()).get();
        assertThat(confirmed.getReservationStatus()).isEqualTo(ReservationStatus.CONFIRMED_PAYMENT_REQUIRED);
    }
}
