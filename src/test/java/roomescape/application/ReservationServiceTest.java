package roomescape.application;

import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.application.dto.request.reservation.ReservationPaymentRequest;
import roomescape.application.dto.request.reservation.ReservationSearchCondition;
import roomescape.application.dto.request.reservation.UserReservationRequest;
import roomescape.application.dto.response.reservation.ReservationResponse;
import roomescape.application.dto.response.reservation.UserReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.ReservationTimeRepository;
import roomescape.domain.reservationdetail.Theme;
import roomescape.domain.reservationdetail.ThemeRepository;
import roomescape.exception.AuthorizationException;
import roomescape.exception.RoomEscapeException;
import roomescape.fixture.CommonFixture;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationDetailFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.support.FakePaymentClient;

class ReservationServiceTest extends BaseServiceTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ReservationDetailRepository reservationDetailRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member user;
    private Member admin;
    private MemberInfo userInfo;
    private MemberInfo adminInfo;
    private ReservationTime time;
    private ReservationDetail detail1;
    private ReservationDetail detail2;
    private Theme theme;

    @BeforeEach
    void setUp() {
        user = memberRepository.save(MemberFixture.user());
        admin = memberRepository.save(MemberFixture.admin());
        userInfo = new MemberInfo(user.getId());
        adminInfo = new MemberInfo(admin.getId());
        time = reservationTimeRepository.save(TimeFixture.createTime(LocalTime.now()));
        theme = themeRepository.save(ThemeFixture.createTheme("테마1"));
        detail1 = reservationDetailRepository.save(ReservationDetailFixture.createReservationDetail(
                CommonFixture.tomorrow, time, theme));
        detail2 = reservationDetailRepository.save(ReservationDetailFixture.createReservationDetail(
                CommonFixture.yesterday, time, theme));
    }

    @DisplayName("예약 성공 시, 결제가 진행되고, 예약 상태로 전환된다")
    @Test
    void when_userReservationSuccess_then_changeToReservedStatus() {
        // given
        UserReservationRequest request = new UserReservationRequest(
                CommonFixture.tomorrow,
                time.getId(),
                theme.getId(),
                CommonFixture.amount,
                CommonFixture.orderId,
                CommonFixture.paymentKey,
                CommonFixture.paymentType);

        // when
        ReservationResponse response = reservationService.reserve(request, userInfo);

        // then
        Reservation reservation = reservationRepository.getById(response.id());
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(reservation.getStatus()).isEqualTo(Status.RESERVED);
            softly.assertThatCode(reservation::getPayment)
                    .doesNotThrowAnyException();
        });
    }

    @DisplayName("결제에 실패하면 예약되지 않는다")
    @Test
    void when_paymentFailed_then_noReservation() {
        // given
        int reservationCount = reservationRepository.findAll().size();
        UserReservationRequest request = new UserReservationRequest(
                CommonFixture.tomorrow,
                time.getId(),
                theme.getId(),
                CommonFixture.amount,
                CommonFixture.orderId,
                FakePaymentClient.getInvalidPaymentKey(),
                CommonFixture.paymentType);

        // when
        try {
            reservationService.reserve(request, userInfo);
        } catch (Exception ignored) {
        }

        // then
        Assertions.assertThat(reservationRepository.findAll())
                .hasSize(reservationCount);
    }

    @DisplayName("예약 대기 시, 결제가 진행되지 않아서 결제 정보가 없다")
    @Test
    void when_userReservationWaiting_then_noPaymentInfo() {
        // given
        UserReservationRequest adminReservationRequest = new UserReservationRequest(
                CommonFixture.tomorrow,
                time.getId(),
                theme.getId(),
                CommonFixture.amount,
                CommonFixture.orderId,
                CommonFixture.paymentKey,
                CommonFixture.paymentType);

        reservationService.reserve(adminReservationRequest, adminInfo);

        UserReservationRequest memberRequest = new UserReservationRequest(
                CommonFixture.tomorrow,
                time.getId(),
                theme.getId(),
                CommonFixture.amount,
                CommonFixture.orderId,
                CommonFixture.paymentKey,
                CommonFixture.paymentType);

        // when
        ReservationResponse response = reservationService.reserve(memberRequest, userInfo);

        // then
        Reservation reservation = reservationRepository.getById(response.id());
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(reservation.getStatus()).isEqualTo(Status.WAITING);
            softly.assertThatThrownBy(reservation::getPayment)
                    .isInstanceOf(RoomEscapeException.class)
                    .hasMessage("결제 정보가 없습니다.");
        });
    }

    @DisplayName("결제 대기 상태의 예약을 결제 시, 예약 상태로 전환된다")
    @Test
    void when_payForPending_then_changeToReservedStatus() {
        // given
        Reservation pendingReservation = reservationRepository.save(
                new Reservation(admin, detail1, Status.PAYMENT_PENDING));

        ReservationPaymentRequest request = new ReservationPaymentRequest(
                pendingReservation.getId(),
                CommonFixture.amount,
                CommonFixture.orderId,
                CommonFixture.paymentKey);

        // when
        ReservationResponse response = reservationService.payForPending(request, adminInfo);

        // then
        Reservation reservation = reservationRepository.getById(response.id());
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(reservation.getStatus()).isEqualTo(Status.RESERVED);
            softly.assertThatCode(reservation::getPayment)
                    .doesNotThrowAnyException();
        });
    }

    @DisplayName("다른 사람의 예약을 결제 시, 권한이 없는 예외가 발생한다")
    @Test
    void when_payForPending_then_throwAuthorizationFailureException() {
        // given
        Reservation pendingReservation = reservationRepository.save(
                new Reservation(admin, detail1, Status.PAYMENT_PENDING));

        ReservationPaymentRequest request = new ReservationPaymentRequest(
                pendingReservation.getId(),
                CommonFixture.amount,
                CommonFixture.orderId,
                CommonFixture.paymentKey);

        // when
        Assertions.assertThatThrownBy(() -> reservationService.payForPending(request, userInfo))
                .isInstanceOf(AuthorizationException.class);
    }

    @DisplayName("예약 상태의 예약을 모두 조회한다")
    @Test
    void when_findAllReservedReservations_then_returnAllReservedReservation() {
        // given
        reservationRepository.save(new Reservation(admin, detail1, Status.CANCELED));
        reservationRepository.save(new Reservation(user, detail1, Status.CANCELED));
        reservationRepository.save(new Reservation(user, detail1, Status.CANCELED));
        reservationRepository.save(new Reservation(admin, detail1, Status.RESERVED));
        reservationRepository.save(new Reservation(user, detail1, Status.WAITING));
        reservationRepository.save(new Reservation(admin, detail1, Status.WAITING));
        reservationRepository.save(new Reservation(admin, detail2, Status.WAITING));
        reservationRepository.save(new Reservation(user, detail2, Status.WAITING));

        // when
        List<ReservationResponse> response = reservationService.findAllReservedReservations();

        // then
        Assertions.assertThat(response).hasSize(1);
    }

    @DisplayName("대기 상태의 예약을 모두 조회한다")
    @Test
    void when_findAllWaitings_then_returnAllWaitingReservation() {
        // given
        reservationRepository.save(new Reservation(admin, detail1, Status.CANCELED));
        reservationRepository.save(new Reservation(user, detail1, Status.CANCELED));
        reservationRepository.save(new Reservation(user, detail1, Status.CANCELED));
        reservationRepository.save(new Reservation(admin, detail1, Status.RESERVED));
        reservationRepository.save(new Reservation(user, detail1, Status.WAITING));
        reservationRepository.save(new Reservation(admin, detail1, Status.WAITING));
        reservationRepository.save(new Reservation(admin, detail2, Status.WAITING));
        reservationRepository.save(new Reservation(user, detail2, Status.WAITING));

        // when
        List<ReservationResponse> response = reservationService.findAllWaitings();

        // then
        Assertions.assertThat(response).hasSize(4);
    }

    @DisplayName("조건에 맞는 예약을 모두 조회한다")
    @Test
    void when_findAllReservationByConditions_then_returnAllReservationByConditions() {
        // given
        reservationRepository.save(new Reservation(admin, detail1, Status.CANCELED));
        reservationRepository.save(new Reservation(user, detail1, Status.CANCELED));
        reservationRepository.save(new Reservation(user, detail1, Status.CANCELED));
        reservationRepository.save(new Reservation(admin, detail1, Status.RESERVED));
        reservationRepository.save(new Reservation(user, detail1, Status.WAITING));
        reservationRepository.save(new Reservation(admin, detail1, Status.WAITING));
        reservationRepository.save(new Reservation(admin, detail2, Status.WAITING));
        reservationRepository.save(new Reservation(user, detail2, Status.RESERVED));

        // when
        List<ReservationResponse> response = reservationService.findAllReservationByConditions(
                new ReservationSearchCondition(CommonFixture.yesterday, CommonFixture.today, user.getId(),
                        theme.getId()));

        // then
        Assertions.assertThat(response).hasSize(1);
    }

    @DisplayName("사용자의 예약을 순번과 함께 조회한다")
    @Test
    void when_findAllWithRank_then_returnAllWithRank() {
        // given
        reservationRepository.save(new Reservation(admin, detail1, Status.CANCELED));
        reservationRepository.save(new Reservation(user, detail1, Status.CANCELED));
        reservationRepository.save(new Reservation(user, detail1, Status.CANCELED));
        reservationRepository.save(new Reservation(admin, detail1, Status.RESERVED));
        reservationRepository.save(new Reservation(user, detail1, Status.WAITING));
        reservationRepository.save(new Reservation(admin, detail1, Status.WAITING));
        reservationRepository.save(new Reservation(admin, detail2, Status.WAITING));
        reservationRepository.save(new Reservation(user, detail2, Status.RESERVED));

        // when
        List<UserReservationResponse> response = reservationService.findAllWithRank(userInfo);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response).hasSize(3);
            softly.assertThat(response.get(0).rank()).isEqualTo(0);
            softly.assertThat(response.get(1).rank()).isEqualTo(0);
            softly.assertThat(response.get(2).rank()).isEqualTo(1);
        });
    }
}
