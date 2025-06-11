package roomescape.reservation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static roomescape.fixture.TestFixture.FUTURE_DATE;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.config.TestConfig;
import roomescape.fixture.TestFixture;
import roomescape.member.application.MemberDataService;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.infrastructure.MemberRepository;
import roomescape.payment.application.PaymentDataService;
import roomescape.payment.application.PaymentApplicationService;
import roomescape.payment.application.client.PaymentClient;
import roomescape.payment.infrastructure.PaymentRepository;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;
import roomescape.reservation.application.dto.request.WaitingConfirmRequest;
import roomescape.reservation.application.dto.request.WaitingReservationCreateRequest;
import roomescape.reservation.application.dto.request.ConfirmedReservationCreateRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.exception.ConfirmedReservationAlreadyExistsException;
import roomescape.reservation.exception.ReservationNotFoundException;
import roomescape.reservation.infrastructure.ReservationRepository;
import roomescape.reservation.presentation.dto.response.WaitingWebResponse;
import roomescape.reservationslot.application.ReservationSlotDataService;
import roomescape.reservationslot.domain.ReservationSlot;
import roomescape.reservationslot.infrastructure.ReservationSlotRepository;
import roomescape.reservationslot.presentation.dto.response.ReservationResponse;
import roomescape.reservationtime.application.ReservationTimeDataService;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.infrastructure.ReservationTimeRepository;
import roomescape.theme.application.ThemeDataService;
import roomescape.theme.domain.Theme;
import roomescape.theme.infrastructure.ThemeRepository;

@DataJpaTest
@Import(TestConfig.class)
class WaitingReservationApplicationServiceTest {

    private static final LocalDateTime afterOneHour = TestFixture.makeTimeAfterOneHour();

    private WaitingReservationApplicationService waitingReservationApplicationService;

    @Autowired
    private ReservationSlotRepository reservationSlotRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private PaymentClient paymentClient;

    private Long timeId;
    private Long themeId;
    private Long memberId;
    private Long memberId2;
    private Long reservationId;

    private ReservationSlotDataService reservationSlotDataService;

    private ConfirmedReservationApplicationService confirmedReservationApplicationService;

    @BeforeEach
    void setUp() {
        reservationSlotDataService = new ReservationSlotDataService(reservationSlotRepository);
        MemberDataService memberDataService = new MemberDataService(memberRepository);
        ReservationDataService reservationDataService = new ReservationDataService(reservationRepository);
        ThemeDataService themeDataService = new ThemeDataService(themeRepository);
        ReservationTimeDataService reservationTimeDataService = new ReservationTimeDataService(
                reservationTimeRepository, reservationSlotDataService);
        PaymentDataService paymentDataService = new PaymentDataService(paymentRepository);
        PaymentApplicationService paymentApplicationService = new PaymentApplicationService(paymentDataService, paymentClient);
        waitingReservationApplicationService = new WaitingReservationApplicationService(
                reservationSlotDataService, memberDataService, reservationDataService, paymentApplicationService);
        confirmedReservationApplicationService = new ConfirmedReservationApplicationService(
                reservationSlotDataService, reservationTimeDataService, themeDataService, memberDataService,
                reservationDataService, paymentApplicationService);

        timeId = reservationTimeRepository.save(new ReservationTime(LocalTime.of(9, 0))).getId();
        themeId = themeRepository.save(TestFixture.makeTheme()).getId();
        memberId = memberRepository.save(TestFixture.makeMember()).getId();
        memberId2 = memberRepository.save(new Member("Free", "free@gmail.com", "password", MemberRole.REGULAR))
                .getId();
        reservationId = confirmedReservationApplicationService.create(
                new ConfirmedReservationCreateRequest(FUTURE_DATE, timeId, themeId, memberId,
                        afterOneHour)).id();
    }

    @Test
    void create_whenValidRequest_returnReservations() {
        // given

        // when
        waitingReservationApplicationService.create(
                new WaitingReservationCreateRequest(FUTURE_DATE, timeId, themeId, memberId2));

        // then
        List<WaitingWebResponse> all = waitingReservationApplicationService.findAll();
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(all.size()).isEqualTo(1);
            softAssertions.assertThat(all.getFirst().name()).isEqualTo("Free");
        });
    }

    @Test
    void findAll() {
        // given
        Long memberId3 = memberRepository.save(new Member("Vector", "vector@gmail.com", "password",
                MemberRole.REGULAR)).getId();
        reservationSlotDataService.getReservationSlotByDateAndTimeAndTheme(FUTURE_DATE, timeId, themeId);
        waitingReservationApplicationService.create(
                new WaitingReservationCreateRequest(FUTURE_DATE, timeId, themeId, memberId2));
        waitingReservationApplicationService.create(
                new WaitingReservationCreateRequest(FUTURE_DATE, timeId, themeId, memberId3));

        // when
        List<WaitingWebResponse> all = waitingReservationApplicationService.findAll();

        // then
        assertThat(all.stream().distinct().count()).isEqualTo(2);
    }

    @Test
    void cancelByReservationSlotIdAndMemberId_whenValidRequest_returnVoid() {
        // Given
        Long memberId3 = memberRepository.save(new Member("Vector", "vector@gmail.com", "password",
                MemberRole.REGULAR)).getId();
        ReservationResponse reservationResponse = waitingReservationApplicationService.create(
                new WaitingReservationCreateRequest(FUTURE_DATE, timeId, themeId, memberId2));
        waitingReservationApplicationService.create(
                new WaitingReservationCreateRequest(FUTURE_DATE, timeId, themeId, memberId3));

        // When
        waitingReservationApplicationService.cancelByReservationSlotIdAndMemberId(
                reservationResponse.reservationSlotId(), memberId2);

        // Then
        List<WaitingWebResponse> all = waitingReservationApplicationService.findAll();
        assertThat(all.size()).isOne();
    }

    @Test
    void cancelByReservationSlotIdAndMemberId_whenInvalidReservationSlotId_returnVoid() {
        Assertions.assertThatThrownBy(
                        () -> waitingReservationApplicationService.cancelByReservationSlotIdAndMemberId(999L, memberId))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessageContaining("존재하지 않는 예약입니다.");
    }

    @Test
    void cancel_whenValidRequest_returnVoid() {
        // Given

        // When
        waitingReservationApplicationService.cancel(reservationId);

        // Then
        List<WaitingWebResponse> all = waitingReservationApplicationService.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    void confirm_whenFirstWaiting_successfullyConfirm() {
        // Given
        Reservation confirmedReservation = reservationRepository.findById(reservationId).get();
        ReservationSlot reservationSlot = confirmedReservation.getReservationSlot();
        Member member = memberRepository.findById(memberId2).get();
        Reservation reservation = reservationSlot.addReservation(member, LocalDateTime.now().plusMinutes(1));
        reservationRepository.save(reservation);
        confirmedReservationApplicationService.cancel(reservationId);
        when(paymentClient.approvePayment(any())).thenAnswer(invocation -> {
            PaymentApproveRequest req = invocation.getArgument(0);
            return new PaymentApproveResponse(req.paymentKey(), req.orderId(), req.amount());
        });

        // When
        waitingReservationApplicationService.confirm(new WaitingConfirmRequest(reservationSlot.getId()), new PaymentApproveRequest("test_payment_key", "RESERVATION_test_order_id", 1_000L));

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(reservationSlot.getReservations()).hasSize(1);
            softAssertions.assertThat(reservationSlot.isConfirmedReservationExist()).isTrue();
        });
    }

    @Test
    void confirm_whenConfirmedReservationExists_shouldThrowException() {
        // Given
        Reservation confirmedReservation = reservationRepository.findById(reservationId).get();
        ReservationSlot reservationSlot = confirmedReservation.getReservationSlot();
        Member member = memberRepository.findById(memberId2).get();
        Reservation reservation = reservationSlot.addReservation(member, LocalDateTime.now().plusMinutes(1));
        reservationRepository.save(reservation);
        when(paymentClient.approvePayment(any())).thenAnswer(invocation -> {
            PaymentApproveRequest req = invocation.getArgument(0);
            return new PaymentApproveResponse(req.paymentKey(), req.orderId(), req.amount());
        });

        // When & Then
        assertThatThrownBy(() -> waitingReservationApplicationService.confirm(new WaitingConfirmRequest(reservationSlot.getId()), new PaymentApproveRequest("test_payment_key", "RESERVATION_test_order_id", 1_000L)))
                .isInstanceOf(ConfirmedReservationAlreadyExistsException.class)
                .hasMessage("이미 예약이 존재하여 진행할 수 없습니다.");
    }

    @Test
    void confirm_whenWaitingNotExists_shouldThrowException() {
        // Given
        ReservationTime reservationTime = reservationTimeRepository.findById(timeId).get();
        Theme theme = new Theme("name", "description", "thumbnail");
        themeRepository.save(theme);
        ReservationSlot reservationSlot = new ReservationSlot(FUTURE_DATE, reservationTime, theme);
        reservationSlotRepository.save(reservationSlot);
        when(paymentClient.approvePayment(any())).thenAnswer(invocation -> {
            PaymentApproveRequest req = invocation.getArgument(0);
            return new PaymentApproveResponse(req.paymentKey(), req.orderId(), req.amount());
        });

        // When & Then
        assertThatThrownBy(() -> waitingReservationApplicationService.confirm(new WaitingConfirmRequest(reservationSlot.getId()), new PaymentApproveRequest("test_payment_key", "RESERVATION_test_order_id", 1_000L)))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessage("예약이 존재하지 않습니다.");
    }
}
