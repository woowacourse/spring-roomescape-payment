package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.PaymentProcessRequest;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationInfo;
import roomescape.domain.ReservationStatus;
import roomescape.domain.ReservationTime;
import roomescape.domain.Role;
import roomescape.domain.Theme;
import roomescape.domain.Waiting;
import roomescape.infrastructure.repository.ReservationRepository;
import roomescape.presentation.dto.request.AdminReservationCreateRequest;
import roomescape.presentation.dto.request.LoginMember;
import roomescape.presentation.dto.request.ReservationWithPaymentRequest;
import roomescape.presentation.dto.response.MemberResponse;
import roomescape.presentation.dto.response.MyReservationResponse;
import roomescape.presentation.dto.response.ReservationResponse;
import roomescape.presentation.dto.response.ReservationTimeResponse;
import roomescape.presentation.dto.response.ThemeResponse;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ReservationTimeService reservationTimeService;

    @Mock
    private ThemeService themeService;

    @Mock
    private CurrentTimeService currentTimeService;

    @Mock
    private WaitingService waitingService;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void 예약을_모두_조회한다() {
        Member member = Member.create("한스", Role.USER, "test@email.com", "pass1");
        LocalDate date = LocalDate.of(2025, 4, 21);
        ReservationTime time = ReservationTime.create(LocalTime.of(10, 0));
        Theme theme = Theme.create("공포", "공포테마", "공포.jpg");
        Reservation reservation = Reservation.create(member, date, time, theme);

        when(reservationRepository.findAllByStatus(ReservationStatus.RESERVED))
                .thenReturn(List.of(reservation));

        List<ReservationResponse> responses = reservationService.getReservations();
        ReservationResponse response = responses.getFirst();

        assertAll(
                () -> assertThat(responses).hasSize(1),
                () -> assertThat(response.date()).isEqualTo(date),
                () -> assertThat(response.time()).isEqualTo(ReservationTimeResponse.from(time)),
                () -> assertThat(response.theme()).isEqualTo(ThemeResponse.from(theme)),
                () -> assertThat(response.member()).isEqualTo(MemberResponse.from(member))
        );
    }

    @Test
    void 사용자가_예약을_생성한다() {
        Member member = Member.create("한스", Role.USER, "test@email.com", "pass1");
        LocalDate date = LocalDate.of(2025, 4, 21);
        ReservationTime time = ReservationTime.create(LocalTime.of(10, 0));
        Theme theme = Theme.create("공포", "공포테마", "공포.jpg");
        Reservation reservation = Reservation.create(member, date, time, theme);

        ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(
                date,
                1L,
                1L,
                "paymentKey",
                "orderId",
                "10000");
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), Role.USER, member.getEmail());

        when(memberService.findMemberByEmail(loginMember.email())).thenReturn(member);
        PaymentProcessRequest paymentRequest = PaymentProcessRequest.of(request);
        Payment payment = Payment.create("paymentKey", "orderId");
        when(paymentService.process(paymentRequest)).thenReturn(payment);
        when(reservationTimeService.findReservationTimeById(request.timeId())).thenReturn(time);
        when(currentTimeService.now()).thenReturn(LocalDateTime.of(2025, 4, 20, 10, 0));
        when(themeService.findThemeById(request.themeId())).thenReturn(theme);
        when(reservationRepository.existsByDateAndTimeAndThemeAndStatus(date, time, theme,
                ReservationStatus.RESERVED)).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationResponse response = reservationService.createMemberReservation(request, loginMember);

        assertAll(
                () -> assertThat(response.date()).isEqualTo(date),
                () -> assertThat(response.time()).isEqualTo(ReservationTimeResponse.from(time)),
                () -> assertThat(response.theme()).isEqualTo(ThemeResponse.from(theme)),
                () -> assertThat(response.member()).isEqualTo(MemberResponse.from(member))
        );
    }

    @Test
    void 관리자가_예약을_생성한다() {
        Member member = Member.create("한스", Role.USER, "test@email.com", "pass1");
        LocalDate date = LocalDate.of(2025, 4, 21);
        ReservationTime time = ReservationTime.create(LocalTime.of(10, 0));
        Theme theme = Theme.create("공포", "공포테마", "공포.jpg");
        Reservation reservation = Reservation.create(member, date, time, theme);

        AdminReservationCreateRequest request = new AdminReservationCreateRequest(
                date,
                1L,
                1L,
                1L);

        when(memberService.findMemberById(request.memberId())).thenReturn(member);
        when(reservationTimeService.findReservationTimeById(request.timeId())).thenReturn(time);
        when(currentTimeService.now()).thenReturn(LocalDateTime.of(2025, 4, 20, 10, 0));
        when(themeService.findThemeById(request.themeId())).thenReturn(theme);
        when(reservationRepository.existsByDateAndTimeAndThemeAndStatus(date, time, theme,
                ReservationStatus.RESERVED)).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationResponse response = reservationService.createAdminReservation(request);

        assertAll(
                () -> assertThat(response.date()).isEqualTo(date),
                () -> assertThat(response.time()).isEqualTo(ReservationTimeResponse.from(time)),
                () -> assertThat(response.theme()).isEqualTo(ThemeResponse.from(theme)),
                () -> assertThat(response.member()).isEqualTo(MemberResponse.from(member))
        );
    }

    @Test
    void 같은일시_같은테마_예약이_존재할때_예약을_생성하면_예외가_발생한다() {
        Member member = Member.create("한스", Role.USER, "test@email.com", "pass1");
        LocalDate date = LocalDate.of(2025, 4, 21);
        ReservationTime time = ReservationTime.create(LocalTime.of(10, 0));
        Theme theme = Theme.create("공포", "공포테마", "공포.jpg");

        ReservationWithPaymentRequest request = new ReservationWithPaymentRequest(
                date,
                1L,
                1L,
                "paymentKey",
                "orderId",
                "10000");
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), Role.USER, member.getEmail());

        when(memberService.findMemberByEmail(loginMember.email())).thenReturn(member);
        PaymentProcessRequest paymentRequest = PaymentProcessRequest.of(request);
        Payment payment = Payment.create("paymentKey", "orderId");
        when(paymentService.process(paymentRequest)).thenReturn(payment);
        when(reservationTimeService.findReservationTimeById(request.timeId())).thenReturn(time);
        when(currentTimeService.now()).thenReturn(LocalDateTime.of(2025, 4, 20, 10, 0));
        when(themeService.findThemeById(request.themeId())).thenReturn(theme);
        when(reservationRepository.existsByDateAndTimeAndThemeAndStatus(date, time, theme,
                ReservationStatus.RESERVED)).thenReturn(true);

        assertThatThrownBy(() -> reservationService.createMemberReservation(request, loginMember))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Transactional
    void 예약을_삭제한다() {
        Member member = Member.create("한스", Role.USER, "test@email.com", "pass1");
        LocalDate date = LocalDate.of(2025, 4, 21);
        ReservationTime time = ReservationTime.create(LocalTime.of(10, 0));
        Theme theme = Theme.create("공포", "공포테마", "공포.jpg");
        Reservation reservation = Reservation.create(member, date, time, theme);
        ReservationInfo reservationInfo = ReservationInfo.create(reservation);

        when(reservationRepository.findById(any())).thenReturn(Optional.of(reservation));
        when(waitingService.existsWaitings(reservationInfo)).thenReturn(false);
        reservationService.cancelReservationById(reservation.getId());

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }

    @Test
    void 존재하지_않는_예약을_삭제하면_예외가_발생한다() {
        when(reservationRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reservationService.cancelReservationById(3L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 조건에따라_예약을_조회한다() {
        Member member = Member.create("한스", Role.USER, "test@email.com", "pass1");
        LocalDate date = LocalDate.of(2025, 4, 21);
        ReservationTime time = ReservationTime.create(LocalTime.of(10, 0));
        Theme theme = Theme.create("공포", "공포테마", "공포.jpg");
        Reservation reservation = Reservation.create(member, date, time, theme);

        LocalDate dateFrom = LocalDate.of(2025, 4, 21);
        LocalDate dateTo = LocalDate.of(2025, 4, 21);

        when(reservationRepository.findAllByThemeAndMemberAndDate(
                any(),
                any(),
                eq(dateFrom),
                eq(dateTo)
        )).thenReturn(List.of(reservation));

        List<ReservationResponse> responses = reservationService.getReservationsByFilter(theme.getId(), member.getId(),
                dateFrom,
                dateTo);
        ReservationResponse response = responses.getFirst();

        assertAll(
                () -> assertThat(responses).hasSize(1),
                () -> assertThat(response.id()).isEqualTo(reservation.getId()),
                () -> assertThat(response.date()).isEqualTo(reservation.getDate())
        );
    }

    @Test
    void 나의_예약_기록을_조회한다() {
        Member member = Member.create("한스", Role.USER, "test1@email.com", "pass1");
        Member anotherMember = Member.create("듀이", Role.USER, "test2@email.com", "pass1");
        LocalDate date = LocalDate.of(2025, 4, 21);
        LocalDate anotherDate = LocalDate.of(2025, 4, 22);
        ReservationTime time = ReservationTime.create(LocalTime.of(10, 0));
        Theme theme = Theme.create("공포", "공포테마", "공포.jpg");
        Reservation reservation = Reservation.create(member, date, time, theme);
        Reservation anotherReservation = Reservation.create(anotherMember, anotherDate, time, theme);
        ReservationInfo anotherReservationInfo = ReservationInfo.create(anotherReservation);
        Waiting waiting = Waiting.create(anotherReservationInfo, member, 1);
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), Role.USER, member.getEmail());

        when(memberService.findMemberById(loginMember.id())).thenReturn(member);
        when(reservationRepository.findAllByMember(member)).thenReturn(List.of(reservation));
        when(waitingService.findWaitingsByMember(member)).thenReturn(List.of(waiting));

        List<MyReservationResponse> responses = reservationService.getMyReservations(loginMember);
        MyReservationResponse reservationResponse = responses.getFirst();
        MyReservationResponse waitingResponse = responses.getLast();

        assertAll(
                () -> assertThat(responses).hasSize(2),
                () -> assertThat(reservationResponse.id()).isEqualTo(reservation.getId()),
                () -> assertThat(reservationResponse.date()).isEqualTo(reservation.getDate()),
                () -> assertThat(reservationResponse.time()).isEqualTo(reservation.getTime().getStartAt()),
                () -> assertThat(reservationResponse.theme()).isEqualTo(reservation.getTheme().getName()),
                () -> assertThat(reservationResponse.status()).isEqualTo(reservation.getStatus().getName()),

                () -> assertThat(waitingResponse.id()).isEqualTo(waiting.getId()),
                () -> assertThat(waitingResponse.date()).isEqualTo(waiting.getReservationInfo().getDate()),
                () -> assertThat(waitingResponse.time()).isEqualTo(waiting.getReservationInfo().getTime().getStartAt()),
                () -> assertThat(waitingResponse.theme()).isEqualTo(waiting.getReservationInfo().getTheme().getName()),
                () -> assertThat(waitingResponse.status()).isEqualTo("1번째 예약대기")
        );
    }
}
