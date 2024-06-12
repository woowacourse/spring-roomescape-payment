package roomescape.application;

import java.time.LocalTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.application.dto.request.reservation.UserReservationRequest;
import roomescape.application.dto.response.reservation.ReservationResponse;
import roomescape.domain.event.TimeoutEventPublisher;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.ReservationTimeRepository;
import roomescape.domain.reservationdetail.Theme;
import roomescape.domain.reservationdetail.ThemeRepository;
import roomescape.fixture.CommonFixture;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationDetailFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;

class CancelServiceTest extends BaseServiceTest {

    @Autowired
    private CancelService cancelService;
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

    @SpyBean
    private PaymentClient paymentClient;
    @MockBean
    private TimeoutEventPublisher eventPublisher;

    private Member user;
    private Member admin;
    private MemberInfo userInfo;
    private MemberInfo adminInfo;
    private ReservationTime time;
    private ReservationDetail detail;
    private Theme theme;

    @BeforeEach
    void setUp() {
        user = memberRepository.save(MemberFixture.user());
        admin = memberRepository.save(MemberFixture.admin());
        userInfo = new MemberInfo(user.getId());
        adminInfo = new MemberInfo(admin.getId());
        time = reservationTimeRepository.save(TimeFixture.createTime(LocalTime.now()));
        theme = themeRepository.save(ThemeFixture.createTheme("테마1"));
        detail = reservationDetailRepository.save(ReservationDetailFixture.createReservationDetail(
                CommonFixture.tomorrow, time, theme));
    }

    @DisplayName("예약 취소 시, 예약 상태가 취소로 변경된다")
    @Test
    void when_cancelReservation_then_changedToCancelStatus() {
        // given
        Reservation reservation = reservationRepository.save(new Reservation(user, detail, Status.RESERVED));

        // when
        cancelService.cancelReservation(reservation.getId(), userInfo);

        // then
        Reservation canceledReservation = reservationRepository.getById(reservation.getId());
        Assertions.assertThat(canceledReservation.getStatus()).isEqualTo(Status.CANCELED);
    }

    @DisplayName("예약 취소 시, 결제도 취소된다")
    @Test
    void when_cancelReservation_then_paymentCanceled() {
        // given
        UserReservationRequest request = new UserReservationRequest(
                CommonFixture.tomorrow,
                time.getId(),
                theme.getId(),
                1000L,
                "asdsds",
                "asddsdsa",
                "dsadsdsa");
        ReservationResponse reserve = reservationService.reserve(request, userInfo);

        // when
        cancelService.cancelReservation(reserve.id(), userInfo);

        // then
        Mockito.verify(paymentClient, Mockito.times(1))
                .cancel(Mockito.any(), Mockito.any());
    }

    @DisplayName("예약 취소 시, 다음 예약 대기가 결제 대기 상태로 전환된다")
    @Test
    void when_cancelReservation_then_nextWaitingReservationChangedToWaitingStatus() {
        // given
        Reservation reservation = reservationRepository.save(new Reservation(admin, detail, Status.RESERVED));
        Reservation nextReservation = reservationRepository.save(new Reservation(user, detail, Status.WAITING));

        // when
        cancelService.cancelReservation(reservation.getId(), adminInfo);

        // then
        Reservation waitingReservation = reservationRepository.getById(nextReservation.getId());
        Assertions.assertThat(waitingReservation.getStatus()).isEqualTo(Status.PAYMENT_PENDING);
    }

    @DisplayName("예약 취소 시, 이벤트가 발행된다")
    @Test
    void when_cancelReservation_then_eventPublished() {
        // given
        Reservation reservation = reservationRepository.save(new Reservation(admin, detail, Status.RESERVED));
        reservationRepository.save(new Reservation(user, detail, Status.WAITING));

        // when
        cancelService.cancelReservation(reservation.getId(), adminInfo);

        // then
        Mockito.verify(eventPublisher, Mockito.times(1))
                .publishTimeoutEvent(Mockito.any());
    }

    @DisplayName("예약 취소 시, 다음 예약 대기가 없으면 아무 일도 일어나지 않는다")
    @Test
    void when_cancelReservation_then_nothingHappened() {
        // given
        Reservation reservation = reservationRepository.save(new Reservation(admin, detail, Status.RESERVED));

        // when
        cancelService.cancelReservation(reservation.getId(), adminInfo);

        // then
        Mockito.verify(eventPublisher, Mockito.times(0))
                .publishTimeoutEvent(Mockito.any());
    }
}
