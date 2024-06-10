package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static roomescape.util.Fixture.HORROR_THEME;
import static roomescape.util.Fixture.JOJO;
import static roomescape.util.Fixture.KAKI;
import static roomescape.util.Fixture.LOGIN_MEMBER_JOJO;
import static roomescape.util.Fixture.LOGIN_MEMBER_KAKI;
import static roomescape.util.Fixture.ORDER_ID;
import static roomescape.util.Fixture.PAYMENT_KEY;
import static roomescape.util.Fixture.RESERVATION_CANCEL_REASON;
import static roomescape.util.Fixture.RESERVATION_HOUR_10;
import static roomescape.util.Fixture.RESERVATION_HOUR_11;
import static roomescape.util.Fixture.TODAY;
import static roomescape.util.Fixture.TOMORROW;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.auth.dto.LoginMember;
import roomescape.common.exception.EntityNotExistException;
import roomescape.config.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentSaveResponse;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.exception.PaymentFailException;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.AdminReservationSaveRequest;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationWaitingResponse;
import roomescape.reservation.dto.UserReservationSaveRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ReservationServiceTest {

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("회원 예약 페이지에서 예약 시 결제에 성공하면 예약을 성공적으로 저장한다.")
    @Test
    void saveUserPageReservation() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);
        Member kaki = memberRepository.save(KAKI);

        LoginMember loginMember = LOGIN_MEMBER_KAKI;
        UserReservationSaveRequest userReservationSaveRequest =
                new UserReservationSaveRequest(TODAY, horrorTheme.getId(), hour10.getId(), PAYMENT_KEY, ORDER_ID, BigDecimal.valueOf(1000));

        TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(ORDER_ID, BigDecimal.valueOf(1000), PAYMENT_KEY);
        PaymentSaveResponse paymentSaveResponse = new PaymentSaveResponse(PAYMENT_KEY, PaymentStatus.PAID, BigDecimal.valueOf(1000));
        doReturn(paymentSaveResponse).when(paymentService)
                .payForReservation(tossPaymentRequest, userReservationSaveRequest.toEntity(kaki, horrorTheme, hour10, ReservationStatus.SUCCESS));

        ReservationResponse reservationResponse = reservationService.saveUserPageReservation(userReservationSaveRequest, loginMember, ReservationStatus.SUCCESS);

        assertThat(reservationResponse.id()).isEqualTo(1L);
    }

    @DisplayName("회원 예약 페이지에서 예약 시 결제에 실패하면 예외가 발생한다.")
    @Test
    void saveUserPageReservationExceptionByPaymentFail() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);
        memberRepository.save(KAKI);

        LoginMember loginMember = LOGIN_MEMBER_KAKI;
        UserReservationSaveRequest userReservationSaveRequest =
                new UserReservationSaveRequest(TODAY, horrorTheme.getId(), hour10.getId(), PAYMENT_KEY, ORDER_ID, BigDecimal.valueOf(1000));

        doThrow(PaymentFailException.class).when(paymentService).payForReservation(any(TossPaymentRequest.class), any(Reservation.class));

        assertThatThrownBy(() -> reservationService.saveUserPageReservation(userReservationSaveRequest, loginMember, ReservationStatus.SUCCESS))
                .isInstanceOf(PaymentFailException.class);
    }

    @DisplayName("관리자가 관리자 페이지에서 예약 시 결제 없이 요청받은 회원의 예약을 대신 해준다.")
    @Test
    void saveAdminPageReservationExceptionByPaymentFail() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);
        Member kaki = memberRepository.save(KAKI);

        AdminReservationSaveRequest adminReservationSaveRequest =
                new AdminReservationSaveRequest(TODAY, horrorTheme.getId(), hour10.getId(), kaki.getId());

        ReservationResponse reservationResponse =
                reservationService.saveAdminPageReservation(adminReservationSaveRequest, ReservationStatus.SUCCESS);

        assertThat(reservationResponse.member().id()).isEqualTo(kaki.getId());
    }

    @DisplayName("회원 예약 페이지에서 예약 시 존재하지 않는 예약 시간에 예약을 하면 예외가 발생한다.")
    @Test
    void notExistReservationTimeIdExceptionTest() {
        Theme horrorTheme = themeRepository.save(HORROR_THEME);

        LoginMember loginMember = LOGIN_MEMBER_KAKI;
        UserReservationSaveRequest userReservationSaveRequest =
                new UserReservationSaveRequest(TODAY, horrorTheme.getId(), 11L, PAYMENT_KEY, ORDER_ID, BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> reservationService.saveUserPageReservation(userReservationSaveRequest, loginMember, ReservationStatus.SUCCESS))
                .isInstanceOf(EntityNotExistException.class);
    }

    @DisplayName("예약 성공 상태의 중복된 예약이 있다면 예외가 발생한다.")
    @Test
    void validateDuplicatedReservationSuccess() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);
        memberRepository.save(KAKI);

        LoginMember loginMember = LOGIN_MEMBER_KAKI;
        UserReservationSaveRequest userReservationSaveRequest =
                new UserReservationSaveRequest(TODAY, horrorTheme.getId(), hour10.getId(), PAYMENT_KEY, ORDER_ID, BigDecimal.valueOf(1000));
        reservationService.saveUserPageReservation(userReservationSaveRequest, loginMember, ReservationStatus.SUCCESS);

        assertThatThrownBy(() -> reservationService.saveUserPageReservation(userReservationSaveRequest, loginMember, ReservationStatus.SUCCESS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("동일한 회원이 예약 대기 상태의 중복된 예약을 할 경우 예외가 발생한다.")
    @Test
    void validateDuplicatedReservationWaiting() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);
        memberRepository.save(KAKI);

        LoginMember loginMember = LOGIN_MEMBER_KAKI;
        UserReservationSaveRequest userReservationSaveRequest =
                new UserReservationSaveRequest(TODAY, horrorTheme.getId(), hour10.getId(), PAYMENT_KEY, ORDER_ID, BigDecimal.valueOf(1000));
        reservationService.saveUserPageReservation(userReservationSaveRequest, loginMember, ReservationStatus.WAIT);

        assertThatThrownBy(() -> reservationService.saveUserPageReservation(userReservationSaveRequest, loginMember, ReservationStatus.WAIT))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("동일한 회원이 예약 후 해당 예약에 연달아 대기를 걸 경우 예외가 발생한다.")
    @Test
    void validateReservationWaitingAfterReservation() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);
        memberRepository.save(KAKI);

        LoginMember loginMember = LOGIN_MEMBER_KAKI;
        UserReservationSaveRequest userReservationSaveRequest =
                new UserReservationSaveRequest(TODAY, horrorTheme.getId(), hour10.getId(), PAYMENT_KEY, ORDER_ID, BigDecimal.valueOf(1000));
        reservationService.saveUserPageReservation(userReservationSaveRequest, loginMember, ReservationStatus.SUCCESS);

        assertThatThrownBy(() -> reservationService.saveUserPageReservation(userReservationSaveRequest, loginMember, ReservationStatus.WAIT))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("회원 별 예약 목록을 조회 시 대기 상태의 예약은 대기 순서를 함께 반환한다.")
    @Test
    void findMemberReservations() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);
        memberRepository.save(KAKI);
        memberRepository.save(JOJO);

        UserReservationSaveRequest userReservationSaveRequest =
                new UserReservationSaveRequest(TODAY, horrorTheme.getId(), hour10.getId(), PAYMENT_KEY, ORDER_ID, BigDecimal.valueOf(1000));

        LoginMember loginMember1 = LOGIN_MEMBER_KAKI;
        reservationService.saveUserPageReservation(userReservationSaveRequest, loginMember1, ReservationStatus.WAIT);

        LoginMember loginMember2 = LOGIN_MEMBER_JOJO;
        reservationService.saveUserPageReservation(userReservationSaveRequest, loginMember2, ReservationStatus.WAIT);

        List<MemberReservationResponse> memberReservationResponses = reservationService.findMemberReservations(loginMember2);

        assertThat(memberReservationResponses).extracting(MemberReservationResponse::rank)
                .containsExactly(2);
    }

    @DisplayName("예약 아이디로 조회 시 존재하지 않는 아이디면 예외가 발생한다.")
    @Test
    void findByIdExceptionTest() {
        assertThatThrownBy(() -> reservationService.findById(1L))
                .isInstanceOf(EntityNotExistException.class);
    }

    @DisplayName("당일 예약을 삭제하면 예외가 발생한다.")
    @Test
    void deleteTodayReservation() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);
        memberRepository.save(KAKI);

        LoginMember loginMember = LOGIN_MEMBER_KAKI;
        UserReservationSaveRequest userReservationSaveRequest =
                new UserReservationSaveRequest(TODAY, horrorTheme.getId(), hour10.getId(), PAYMENT_KEY, ORDER_ID, BigDecimal.valueOf(1000));
        ReservationResponse reservationResponse = reservationService.saveUserPageReservation(userReservationSaveRequest, loginMember, ReservationStatus.SUCCESS);

        assertThatThrownBy(() -> reservationService.cancelById(reservationResponse.id(), RESERVATION_CANCEL_REASON))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("확정된 예약을 취소하면 예약 상태가 CANCEL로 변경되고, 예약 대기자가 있다면 첫 번째 대기자가 결제 대기 상태가 된다.")
    @Test
    void cancelById() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);

        Member kaki = memberRepository.save(KAKI);
        Member jojo = memberRepository.save(JOJO);

        Reservation reservation1 = new Reservation(kaki, TOMORROW, horrorTheme, hour10, ReservationStatus.SUCCESS);
        Reservation reservation2 = new Reservation(jojo, TOMORROW, horrorTheme, hour10, ReservationStatus.WAIT);

        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);

        reservationService.cancelById(kaki.getId(), RESERVATION_CANCEL_REASON);
        Reservation jojoReservation = reservationRepository.findById(jojo.getId()).get();

        assertThat(jojoReservation.getStatus()).isEqualTo(ReservationStatus.PAYMENT_PENDING);
    }

    @DisplayName("현재 날짜 이후의 예약들을 예약 날짜, 예약 시간, 예약 추가 순으로 정렬해 예약 대기 목록을 조회한다.")
    @Test
    void findWaitingReservations() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        ReservationTime hour11 = reservationTimeRepository.save(RESERVATION_HOUR_11);

        Theme horrorTheme = themeRepository.save(HORROR_THEME);

        Member kaki = memberRepository.save(KAKI);
        Member jojo = memberRepository.save(JOJO);

        Reservation reservation1 = new Reservation(kaki, TOMORROW, horrorTheme, hour11, ReservationStatus.WAIT);
        Reservation reservation2 = new Reservation(kaki, TODAY, horrorTheme, hour10, ReservationStatus.WAIT);
        Reservation reservation3 = new Reservation(kaki, TODAY, horrorTheme, hour11, ReservationStatus.WAIT);
        Reservation reservation4 = new Reservation(jojo, TOMORROW, horrorTheme, hour10, ReservationStatus.WAIT);

        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);
        reservationRepository.save(reservation4);

        List<ReservationWaitingResponse> waitingReservations = reservationService.findWaitingReservations();

        assertThat(waitingReservations).extracting(ReservationWaitingResponse::id)
                .containsExactly(reservation2.getId(), reservation3.getId(), reservation4.getId(), reservation1.getId());
    }
}
