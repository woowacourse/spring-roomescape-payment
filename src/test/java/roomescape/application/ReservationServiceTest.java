package roomescape.application;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.application.dto.PaymentProcessRequest;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;
import roomescape.domain.ReservationTime;
import roomescape.domain.Role;
import roomescape.domain.Theme;
import roomescape.infrastructure.repository.ReservationRepository;
import roomescape.presentation.dto.request.AdminReservationCreateRequest;
import roomescape.presentation.dto.request.LoginMember;
import roomescape.presentation.dto.request.ReservationWithPaymentRequest;
import roomescape.presentation.dto.response.MemberResponse;
import roomescape.presentation.dto.response.ReservationResponse;
import roomescape.presentation.dto.response.ReservationTimeResponse;
import roomescape.presentation.dto.response.ThemeResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationResponse response = reservationService.createAdminReservation(request);

        assertAll(
                () -> assertThat(response.date()).isEqualTo(date),
                () -> assertThat(response.time()).isEqualTo(ReservationTimeResponse.from(time)),
                () -> assertThat(response.theme()).isEqualTo(ThemeResponse.from(theme)),
                () -> assertThat(response.member()).isEqualTo(MemberResponse.from(member))
        );
    }
//
//    @Test
//    void 같은일시_다른테마_예약은_생성할_수_있다() {
//        ReservationTime reservationTime = reservationTimeDbFixture.예약시간_10시();
//        Theme horror = themeDbFixture.공포();
//        Theme mystery = themeDbFixture.커스텀_테마("미스테리");
//        Member member = memberDbFixture.한스_사용자();
//        reservationDbFixture.예약_한스_25_4_22_10시_공포(member, reservationTime, horror);
//
//        ReservationCreateRequest request = new ReservationCreateRequest(
//                ReservationDateFixture.예약날짜_25_4_22.getDate(),
//                reservationTime.getId(),
//                mystery.getId()
//        );
//        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), Role.USER, member.getEmail());
//
//        assertThatCode(() -> reservationService.createMemberReservation(request, loginMember))
//                .doesNotThrowAnyException();
//    }
//
//    @Test
//    void 같은일시_같은테마_예약이_존재할때_예약을_생성하면_예외가_발생한다() {
//        ReservationTime reservationTime = reservationTimeDbFixture.예약시간_10시();
//        Theme theme = themeDbFixture.공포();
//        Member member = memberDbFixture.한스_사용자();
//        reservationDbFixture.예약_한스_25_4_22_10시_공포(member, reservationTime, theme);
//
//        ReservationCreateRequest request = new ReservationCreateRequest(
//                ReservationDateFixture.예약날짜_25_4_22.getDate(),
//                reservationTime.getId(),
//                theme.getId()
//        );
//        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), Role.USER, member.getEmail());
//
//        assertThatThrownBy(() -> reservationService.createMemberReservation(request, loginMember))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @Test
//    @Transactional
//    void 예약을_삭제한다() {
//        ReservationTime reservationTime = reservationTimeDbFixture.예약시간_10시();
//        Theme theme = themeDbFixture.공포();
//        Member member = memberDbFixture.한스_사용자();
//        Reservation reservation = reservationDbFixture.예약_한스_25_4_22_10시_공포(member, reservationTime, theme);
//
//        reservationService.cancelReservationById(reservation.getId());
//
//        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
//    }
//
//    @Test
//    void 존재하지_않는_예약을_삭제하면_예외가_발생한다() {
//        assertThatThrownBy(() -> reservationService.cancelReservationById(3L))
//                .isInstanceOf(NoSuchElementException.class);
//    }
//
//    @Test
//    void 조건에따라_예약을_조회한다() {
//        ReservationTime reservationTime = reservationTimeDbFixture.예약시간_10시();
//        Theme theme = themeDbFixture.공포();
//        Member member = memberDbFixture.한스_사용자();
//        Reservation reservation = reservationDbFixture.예약_한스_25_4_22_10시_공포(member, reservationTime, theme);
//
//        Long themeId = theme.getId();
//        Long memberId = member.getId();
//        LocalDate dateFrom = LocalDate.of(2025, 4, 22);
//        LocalDate dateTo = LocalDate.of(2025, 4, 22);
//
//        List<ReservationResponse> responses = reservationService.getReservationsByFilter(themeId, memberId, dateFrom, dateTo);
//        ReservationResponse response = responses.getFirst();
//
//        assertAll(
//                () -> assertThat(responses).hasSize(1),
//                () -> assertThat(response.id()).isEqualTo(reservation.getId()),
//                () -> assertThat(response.date()).isEqualTo(reservation.getDate())
//        );
//    }
//
//    @Test
//    void 나의_예약_기록을_조회한다() {
//        ReservationTime reservationTime = reservationTimeDbFixture.예약시간_10시();
//        Theme theme = themeDbFixture.공포();
//        Member member = memberDbFixture.한스_사용자();
//        Reservation reservation = reservationDbFixture.예약_한스_25_4_22_10시_공포(member, reservationTime, theme);
//
//        Member alreadyReservedMember = memberDbFixture.듀이_사용자();
//        Reservation alreadyReservedReservation = reservationDbFixture.예약_생성(alreadyReservedMember, ReservationDateFixture.예약날짜_25_4_23, reservationTime, theme);
//        ReservationInfo reservationInfo = ReservationInfo.create(alreadyReservedReservation);
//        Waiting waiting = waitingDbFixture.첫번째_대기(reservationInfo, member);
//
//        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), Role.USER, member.getEmail());
//
//        List<MyReservationResponse> responses = reservationService.getMyReservations(loginMember);
//        MyReservationResponse reservationResponse = responses.getFirst();
//        MyReservationResponse waitingResponse = responses.getLast();
//
//        assertAll(
//                () -> assertThat(responses).hasSize(2),
//                () -> assertThat(reservationResponse.id()).isEqualTo(reservation.getId()),
//                () -> assertThat(reservationResponse.date()).isEqualTo(reservation.getDate()),
//                () -> assertThat(reservationResponse.time()).isEqualTo(reservation.getTime().getStartAt()),
//                () -> assertThat(reservationResponse.theme()).isEqualTo(reservation.getTheme().getName()),
//                () -> assertThat(reservationResponse.status()).isEqualTo(reservation.getStatus().getName()),
//
//                () -> assertThat(waitingResponse.id()).isEqualTo(waiting.getId()),
//                () -> assertThat(waitingResponse.date()).isEqualTo(waiting.getReservationInfo().getDate()),
//                () -> assertThat(waitingResponse.time()).isEqualTo(waiting.getReservationInfo().getTime().getStartAt()),
//                () -> assertThat(waitingResponse.theme()).isEqualTo(waiting.getReservationInfo().getTheme().getName()),
//                () -> assertThat(waitingResponse.status()).isEqualTo("1번째 예약대기")
//        );
//    }
}
