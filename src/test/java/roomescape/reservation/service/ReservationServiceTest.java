package roomescape.reservation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import roomescape.auth.domain.AuthInfo;
import roomescape.common.config.PaymentClientConfig;
import roomescape.common.exception.ForbiddenException;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationTimeFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.FakePaymentClient;
import roomescape.payment.client.PaymentProperties;
import roomescape.payment.model.Payment;
import roomescape.payment.model.PaymentStatus;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.request.CreateMyReservationRequest;
import roomescape.reservation.dto.response.CreateReservationResponse;
import roomescape.reservation.dto.response.FindAdminReservationResponse;
import roomescape.reservation.dto.response.FindAvailableTimesResponse;
import roomescape.reservation.dto.response.FindReservationResponse;
import roomescape.reservation.dto.response.FindReservationWithPaymentResponse;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.model.ReservationWithPayment;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.model.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.model.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.util.DatabaseIsolation;
import roomescape.util.JpaRepositoryTest;
import roomescape.waiting.model.Waiting;
import roomescape.waiting.repository.WaitingRepository;
import roomescape.waiting.service.WaitingService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JpaRepositoryTest
@DatabaseIsolation
@Import({ReservationService.class,
        WaitingService.class,
        FakePaymentClient.class,
        PaymentService.class,
        PaymentClientConfig.class})
@EnableConfigurationProperties({PaymentProperties.class})
class ReservationServiceTest {

    private final ReservationService reservationService;

    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    ReservationServiceTest(final ReservationService reservationService,
                           final ReservationRepository reservationRepository,
                           final ReservationTimeRepository reservationTimeRepository,
                           final ThemeRepository themeRepository,
                           final MemberRepository memberRepository,
                           final WaitingRepository waitingRepository, PaymentRepository paymentRepository) {
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
        this.paymentRepository = paymentRepository;
    }

    @Test
    @DisplayName("회원 예약 생성 성공")
    void createReservation() {
        // given
        ReservationTime reservationTime = reservationTimeRepository.save(ReservationTimeFixture.getOne());
        Theme theme = themeRepository.save(ThemeFixture.getOne());
        Member member = memberRepository.save(MemberFixture.getOne());

        CreateMyReservationRequest createReservationRequest = new CreateMyReservationRequest(
                LocalDate.of(2024, 10, 10), reservationTime.getId(), theme.getId(),
                "paymentKey", "orderId", 100_000L);
        AuthInfo authInfo = new AuthInfo(member.getId(), member.getName(), member.getRole());

        // when
        CreateReservationResponse createReservationResponse = reservationService.createMyReservationWithPayment(
                authInfo,
                createReservationRequest);

        // then
        assertThat(createReservationResponse.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("회원 예약 생성 실패: 없는 시간")
    void createReservation_ifReservationTimeNotExist_throwException() {
        // given
        Theme theme = themeRepository.save(ThemeFixture.getOne());
        Member member = memberRepository.save(MemberFixture.getOne());

        CreateMyReservationRequest createReservationRequest = new CreateMyReservationRequest(
                LocalDate.of(2024, 10, 10), 1L, theme.getId(), "paymentKey", "orderId", 100_000L);
        AuthInfo authInfo = new AuthInfo(member.getId(), member.getName(), member.getRole());

        // when & then
        assertThatThrownBy(() -> reservationService.createMyReservationWithPayment(authInfo, createReservationRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("식별자 1에 해당하는 시간이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원 예약 생성 실패: 없는 테마")
    void createReservation_ifThemeNotExist_throwException() {
        // given
        ReservationTime reservationTime = reservationTimeRepository.save(ReservationTimeFixture.getOne());
        Member member = memberRepository.save(MemberFixture.getOne());

        CreateMyReservationRequest createReservationRequest = new CreateMyReservationRequest(
                LocalDate.of(2024, 10, 10), reservationTime.getId(), 1L, "paymentKey", "orderId", 100_000L);
        AuthInfo authInfo = new AuthInfo(member.getId(), member.getName(), member.getRole());

        // when & then
        assertThatThrownBy(() -> reservationService.createMyReservationWithPayment(authInfo, createReservationRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("식별자 1에 해당하는 테마가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원 예약 생성 실패: 중복 예약")
    void createReservation_ifExistSameDateAndTime_throwException() {
        // given
        LocalDate sameDate = LocalDate.parse("2024-10-10");
        ReservationTime sameReservationTime = reservationTimeRepository.save(ReservationTimeFixture.getOne());
        Theme sameTheme = themeRepository.save(ThemeFixture.getOne());
        List<Member> members = MemberFixture.get(2).stream().map(memberRepository::save).toList();
        reservationRepository.save(new Reservation(members.get(0), sameDate, sameReservationTime, sameTheme));

        CreateMyReservationRequest createReservationRequest = new CreateMyReservationRequest(
                sameDate, sameReservationTime.getId(), sameTheme.getId(), "paymentKey", "orderId", 100_000L);
        AuthInfo authInfo = new AuthInfo(members.get(1).getId(), members.get(1).getName(), members.get(1).getRole());

        // when & then
        assertThatThrownBy(() -> reservationService.createMyReservationWithPayment(authInfo, createReservationRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 2024-10-10의 테마 이름 테마에는 10:00 시의 예약이 존재하여 예약을 생성할 수 없습니다.");
    }

    @Test
    @DisplayName("회원 예약 생성 실패: 지나간 시간")
    void createReservation_validateReservationDateTime_throwException() {
        // given
        LocalDate sameDate = LocalDate.parse("2024-04-10");
        ReservationTime sameReservationTime = reservationTimeRepository.save(ReservationTimeFixture.getOne());
        Theme sameTheme = themeRepository.save(ThemeFixture.getOne());
        List<Member> members = MemberFixture.get(2).stream().map(memberRepository::save).toList();

        CreateMyReservationRequest createReservationRequest = new CreateMyReservationRequest(
                sameDate, sameReservationTime.getId(), sameTheme.getId(), "paymentKey", "orderId", 100_000L);
        AuthInfo authInfo = new AuthInfo(members.get(1).getId(), members.get(1).getName(), members.get(1).getRole());

        // when & then
        assertThatThrownBy(() -> reservationService.createMyReservationWithPayment(authInfo, createReservationRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("2024-04-10는 지나간 시간임으로 예약 생성이 불가능합니다. 현재 이후 날짜로 재예약해주세요.");
    }

    @Test
    @DisplayName("전체 예약 목록 조회 성공")
    void getReservations() {
        // give
        LocalDate date = LocalDate.parse("2024-04-10");
        Member member = memberRepository.save(MemberFixture.getOne());
        ReservationTime reservationTime = reservationTimeRepository.save(ReservationTimeFixture.getOne());
        Theme theme = themeRepository.save(ThemeFixture.getOne());
        Reservation reservation1 = reservationRepository.save(new Reservation(member, date, reservationTime, theme));
        Reservation reservation2 = reservationRepository.save(new Reservation(member, date, reservationTime, theme));

        // when & then
        assertThat(reservationService.getReservations()).containsExactly(
                FindAdminReservationResponse.from(reservation1),
                FindAdminReservationResponse.from(reservation2));
    }

    @Test
    @DisplayName("예약 단건 조회 성공")
    void getReservation() {
        // given
        Member member = memberRepository.save(MemberFixture.getOne());
        ReservationTime reservationTime = reservationTimeRepository.save(ReservationTimeFixture.getOne());
        Theme theme = themeRepository.save(ThemeFixture.getOne());
        Reservation reservation = reservationRepository.save(
                new Reservation(member, LocalDate.parse("2024-04-10"), reservationTime, theme));

        // when & then
        assertThat(reservationService.getReservation(reservation.getId()))
                .isEqualTo(FindReservationResponse.from(reservation));
    }

    @Test
    @DisplayName("예약 단건 조회 실패: 없는 예약")
    void getReservation_ifNotExist_throwException() {
        // when & then
        assertThatThrownBy(() -> reservationService.getReservation(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("식별자 1에 해당하는 예약이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("내 예약 목록 조회 성공")
    void getReservationsByMember() {
        // given
        Member member = memberRepository.save(MemberFixture.getOne());
        ReservationTime reservationTime = reservationTimeRepository.save(ReservationTimeFixture.getOne());
        Theme theme = themeRepository.save(ThemeFixture.getOne());

        Reservation reservation1 = new Reservation(member, LocalDate.parse("9999-04-10"), reservationTime, theme);
        Reservation savedReservation1 = reservationRepository.save(reservation1);
        Payment payment1 = paymentRepository.save(new Payment(savedReservation1, "paymentKey", "orderID", 1000L, PaymentStatus.SUCCESS));

        Reservation reservation2 = new Reservation(member, LocalDate.parse("9999-04-11"), reservationTime, theme);
        Reservation savedReservation2 = reservationRepository.save(reservation2);
        Payment payment2 = paymentRepository.save(new Payment(savedReservation2, "paymentKey", "orderID", 1000L, PaymentStatus.SUCCESS));
        AuthInfo authInfo = new AuthInfo(member.getId(), member.getName(), member.getRole());

        // when & then
        assertThat(reservationService.getReservations(authInfo))
                .containsExactly(FindReservationWithPaymentResponse.from(new ReservationWithPayment(savedReservation1, payment1)),
                        FindReservationWithPaymentResponse.from(new ReservationWithPayment(savedReservation2, payment2)));
    }

    @Test
    @DisplayName("가능한 예약 시간 조회 성공")
    void getAvailableTimes() {
        LocalDate date = LocalDate.parse("2024-10-23");
        Member member = memberRepository.save(MemberFixture.getOne());
        ReservationTime reservationTime1 = reservationTimeRepository.save(
                new ReservationTime(LocalTime.parse("10:00")));
        ReservationTime reservationTime2 = reservationTimeRepository.save(
                new ReservationTime(LocalTime.parse("20:00")));
        Theme theme = themeRepository.save(ThemeFixture.getOne());

        Reservation reservation = reservationRepository.save(
                new Reservation(member, date, reservationTime1, theme));

        // when & then
        assertThat(reservationService.getAvailableTimes(date, theme.getId()))
                .isEqualTo(List.of(
                        FindAvailableTimesResponse.from(reservation.getReservationTime(), true),
                        FindAvailableTimesResponse.from(reservationTime2, false))
                );
    }

    @Test
    @DisplayName("회원, 테마, 기간에 따른 검색 성공")
    void searchBy() {
        // give
        List<Member> members = MemberFixture.get(2).stream().map(memberRepository::save).toList();
        ReservationTime reservationTime = reservationTimeRepository.save(ReservationTimeFixture.getOne());
        Theme theme = themeRepository.save(ThemeFixture.getOne());

        LocalDate dateFrom = LocalDate.parse("2025-04-10");
        LocalDate dateTo = LocalDate.parse("2026-04-10");
        Reservation reservation1 = reservationRepository.save(
                new Reservation(members.get(0), dateFrom, reservationTime, theme));
        Reservation reservation2 = reservationRepository.save(
                new Reservation(members.get(0), dateTo, reservationTime, theme));
        Reservation reservation3 = reservationRepository.save(
                new Reservation(members.get(1), dateTo, reservationTime, theme));

        // when & then
        assertThat(reservationService.searchBy(theme.getId(), members.get(0).getId(), dateFrom, dateTo))
                .containsExactly(
                        FindReservationResponse.from(reservation1),
                        FindReservationResponse.from(reservation2));
    }

    @Test
    @DisplayName("예약 취소 성공: 내 예약")
    void cancelReservation() {
        // given
        Member member = memberRepository.save(MemberFixture.getOne());
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.parse("10:00")));
        Theme theme = themeRepository.save(ThemeFixture.getOne());
        Reservation reservation = reservationRepository.save(
                new Reservation(member, LocalDate.parse("2024-04-10"), reservationTime, theme));

        // when
        AuthInfo authInfo = new AuthInfo(member.getId(), member.getName(), member.getRole());
        reservationService.deleteReservation(authInfo, reservation.getId());

        // then
        assertThat(reservationRepository.findAllByMemberId(member.getId())).isEmpty();
    }

    @Test
    @DisplayName("예약 취소 성공: 어드민 권한")
    void cancelReservation_WhenRoleIsAdmin_Success() {
        // given
        Member admin = memberRepository.save(MemberFixture.getAdmin());
        Member member = memberRepository.save(MemberFixture.getOne());
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.parse("10:00")));
        Theme theme = themeRepository.save(ThemeFixture.getOne());
        Reservation reservation = reservationRepository.save(
                new Reservation(member, LocalDate.parse("2024-04-10"), reservationTime, theme));

        // when
        AuthInfo authInfo = new AuthInfo(admin.getId(), admin.getName(), admin.getRole());
        reservationService.deleteReservation(authInfo, reservation.getId());

        // then
        assertThat(reservationRepository.findAllByMemberId(member.getId())).isEmpty();
    }

    @Test
    @DisplayName("예약 취소에 따른 회원 갱신 성공")
    void cancelReservation_WhenWaitingExists() {
        // given
        Member reservationMember = memberRepository.save(MemberFixture.getOne("reservation@member.com"));
        Member waitingMember = memberRepository.save(MemberFixture.getOne("waiting@member.com"));
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.parse("10:00")));
        Theme theme = themeRepository.save(ThemeFixture.getOne());
        Reservation reservation = reservationRepository.save(
                new Reservation(reservationMember, LocalDate.parse("2024-04-10"), reservationTime, theme));

        waitingRepository.save(new Waiting(reservation, waitingMember));

        // when
        AuthInfo authInfo = new AuthInfo(reservationMember.getId(), reservationMember.getName(),
                reservationMember.getRole());
        reservationService.deleteReservation(authInfo, reservation.getId());

        // then
        assertThat(reservationRepository.getById(reservation.getId()).getMember()).isEqualTo(waitingMember);
    }

    @Test
    @DisplayName("예약 취소 성공: 권한 없음")
    void cancelReservation_WhenForbidden_throwException() {
        // given
        Member member = memberRepository.save(MemberFixture.getOne());
        Member otherMember = memberRepository.save(MemberFixture.getOne("otherMember@nn.com"));
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.parse("10:00")));
        Theme theme = themeRepository.save(ThemeFixture.getOne());
        Reservation reservation = reservationRepository.save(
                new Reservation(member, LocalDate.parse("2024-04-10"), reservationTime, theme));

        // when
        AuthInfo authInfo = new AuthInfo(otherMember.getId(), otherMember.getName(), otherMember.getRole());

        // then
        assertThatThrownBy(() -> reservationService.deleteReservation(authInfo, reservation.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("식별자 1인 예약에 대해 회원 식별자 2의 권한이 존재하지 않아, 삭제가 불가능합니다.");
    }
}
