package roomescape.reservation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static roomescape.fixture.TestFixture.FUTURE_DATE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
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
import roomescape.member.exception.MemberNotFoundException;
import roomescape.member.infrastructure.MemberRepository;
import roomescape.payment.application.PaymentService;
import roomescape.payment.application.client.PaymentClient;
import roomescape.payment.application.infrastructure.PaymentRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentType;
import roomescape.reservation.application.dto.request.ConfirmedReservationByCriteriaWebRequest;
import roomescape.reservation.application.dto.request.ConfirmedReservationCreateRequest;
import roomescape.reservation.application.dto.request.WaitingReservationCreateRequest;
import roomescape.reservation.application.event.ReservationPromoteEvent;
import roomescape.reservation.application.event.TestEventPublisher;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.infrastructure.ReservationRepository;
import roomescape.reservation.presentation.dto.response.ConfirmedReservationWebResponse;
import roomescape.reservationslot.application.ReservationSlotDataService;
import roomescape.reservationslot.infrastructure.ReservationSlotRepository;
import roomescape.reservationslot.presentation.dto.response.MyReservationResponse;
import roomescape.reservationtime.application.ReservationTimeDataService;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.exception.ReservationTimeNotFoundException;
import roomescape.reservationtime.infrastructure.ReservationTimeRepository;
import roomescape.theme.application.ThemeDataService;
import roomescape.theme.domain.Theme;
import roomescape.theme.exception.ThemeNotFoundException;
import roomescape.theme.infrastructure.ThemeRepository;

@DataJpaTest
@Import(TestConfig.class)
class ConfirmedReservationApplicationServiceTest {

    private static final LocalDateTime afterOneHour = TestFixture.makeTimeAfterOneHour();

    private ConfirmedReservationApplicationService confirmedReservationApplicationService;

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

    @Autowired
    private TestEventPublisher eventPublisher;

    private Long timeId;
    private Long themeId;
    private Long memberId;
    private Long memberId2;
    private Long reservationId;

    private WaitingReservationApplicationService waitingReservationApplicationService;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        ReservationSlotDataService reservationSlotDataService = new ReservationSlotDataService(
                reservationSlotRepository);
        MemberDataService memberDataService = new MemberDataService(memberRepository);
        ReservationDataService reservationDataService = new ReservationDataService(reservationRepository);
        ThemeDataService themeDataService = new ThemeDataService(themeRepository);
        ReservationTimeDataService reservationTimeDataService = new ReservationTimeDataService(
                reservationTimeRepository, reservationSlotDataService);
        confirmedReservationApplicationService = new ConfirmedReservationApplicationService(
                reservationSlotDataService,
                reservationTimeDataService, themeDataService,
                memberDataService, reservationDataService, eventPublisher);
        waitingReservationApplicationService = new WaitingReservationApplicationService(reservationSlotDataService,
                memberDataService, reservationDataService);
        paymentService = new PaymentService(paymentClient, paymentRepository, eventPublisher);

        timeId = reservationTimeRepository.save(new ReservationTime(LocalTime.of(9, 0))).getId();
        themeId = themeRepository.save(TestFixture.makeTheme()).getId();
        memberId = memberRepository.save(TestFixture.makeMember()).getId();
        memberId2 = memberRepository.save(new Member("Free", "free@gmail.com", "password", MemberRole.REGULAR)).getId();
        reservationId = confirmedReservationApplicationService.create(
                new ConfirmedReservationCreateRequest(FUTURE_DATE, timeId, themeId, memberId,
                        afterOneHour, null)).id();
        eventPublisher.clear();
    }

    @Test
    void create_whenValidRequest_returnReservations() {
        // when
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        confirmedReservationApplicationService.create(
                new ConfirmedReservationCreateRequest(tomorrow, timeId, themeId, memberId,
                        afterOneHour, null));

        // then
        List<ConfirmedReservationWebResponse> result = confirmedReservationApplicationService.findByCriteria(
                new ConfirmedReservationByCriteriaWebRequest(null, null, tomorrow, tomorrow.plusDays(1)));

        ConfirmedReservationWebResponse response = result.getFirst();
        Assertions.assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(response.member().id()).isEqualTo(memberId),
                () -> assertThat(response.date()).isEqualTo(tomorrow),
                () -> assertThat(response.time().startAt()).isEqualTo(LocalTime.of(9, 0))
        );
    }

    @Test
    void create_whenTimeIdNotFound_throwsReservationTimeNotFoundException() {
        assertThatThrownBy(
                () -> confirmedReservationApplicationService.create(
                        new ConfirmedReservationCreateRequest(FUTURE_DATE, 999L, themeId, memberId,
                                afterOneHour, null)))
                .isInstanceOf(ReservationTimeNotFoundException.class)
                .hasMessageContaining("요청한 id와 일치하는 예약 시간 정보가 없습니다.");
    }

    @Test
    void create_whenThemeIdNotFound_throwsThemeNotFoundException() {
        assertThatThrownBy(
                () -> confirmedReservationApplicationService.create(
                        new ConfirmedReservationCreateRequest(FUTURE_DATE, timeId, 999L, memberId,
                                afterOneHour, null)))
                .isInstanceOf(ThemeNotFoundException.class)
                .hasMessageContaining("요청한 id와 일치하는 테마 정보가 없습니다.");
    }

    @Test
    void create_whenMemberIdNotFound_throwsMemberNotFoundException() {
        assertThatThrownBy(
                () -> waitingReservationApplicationService.create(
                        new WaitingReservationCreateRequest(FUTURE_DATE, timeId, themeId, 999L)))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("존재하지 않은 멤버입니다.");
    }

    @Test
    void findByCriteria_whenValidRequest_returnsReservation() {
        // when
        Long themeId2 = themeRepository.save(new Theme("논리", "논리 게임 with Danny", "image.png")).getId();
        ConfirmedReservationWebResponse response = confirmedReservationApplicationService.create(
                new ConfirmedReservationCreateRequest(FUTURE_DATE, timeId, themeId2, memberId, afterOneHour, null));

        // then
        List<ConfirmedReservationWebResponse> result = confirmedReservationApplicationService.findByCriteria(
                new ConfirmedReservationByCriteriaWebRequest(themeId2, null, null, null));

        Assertions.assertAll(
                () -> assertThat(response.member().name()).isEqualTo("Mint"),
                () -> assertThat(response.date()).isEqualTo(FUTURE_DATE),
                () -> assertThat(response.time().startAt()).isEqualTo(LocalTime.of(9, 0)),
                () -> assertThat(result).hasSize(1)
        );
    }

    @Test
    void findByCriteria_whenNoCondition_returnAllReservations() {
        // given
        Long timeId2 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 0))).getId();
        confirmedReservationApplicationService.create(
                new ConfirmedReservationCreateRequest(FUTURE_DATE, timeId2, themeId, memberId,
                        afterOneHour, null));
        // when
        List<ConfirmedReservationWebResponse> result = confirmedReservationApplicationService.findByCriteria(
                new ConfirmedReservationByCriteriaWebRequest(null, null, null, null));

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void findByCriteria_whenHasCondition_shouldReturnFilteredReservations() {
        // given
        Long themeId2 = themeRepository.save(new Theme("논리", "논리 게임 with Danny", "image.png")).getId();
        ConfirmedReservationWebResponse response = confirmedReservationApplicationService.create(
                new ConfirmedReservationCreateRequest(FUTURE_DATE, timeId, themeId2, memberId, afterOneHour, null));

        // when
        List<ConfirmedReservationWebResponse> result = confirmedReservationApplicationService.findByCriteria(
                new ConfirmedReservationByCriteriaWebRequest(themeId2, null, null, null));

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void cancel_whenReservationExists_removesSuccessfully() {
        // given

        // when
        confirmedReservationApplicationService.cancel(reservationId);

        // then
        List<ConfirmedReservationWebResponse> result = confirmedReservationApplicationService.findByCriteria(
                new ConfirmedReservationByCriteriaWebRequest(themeId, memberId, FUTURE_DATE, FUTURE_DATE.plusDays(1)));
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(result).isEmpty();
            softAssertions.assertThat(reservationSlotRepository.findById(reservationId)).isEmpty();
        });
    }

    @Test
    void cancel_shouldPublishReservationPromoteEvent_whenReservationMoreThanTwo() {
        // given
        waitingReservationApplicationService.create(
                new WaitingReservationCreateRequest(FUTURE_DATE, timeId, themeId, memberId2));

        // when
        confirmedReservationApplicationService.cancel(reservationId);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(eventPublisher.hasEvent(ReservationPromoteEvent.class)).isTrue();
            softAssertions.assertThat(eventPublisher.getEventsOfType(ReservationPromoteEvent.class)).hasSize(1);
        });
    }

    @Test
    void findReservations_shouldReturnMemberReservationsByMemberId() {
        // given
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        Payment payment = paymentService.save(new Payment("PAYMENT_KEY", "ORDER_ID", 5000L, PaymentType.NORMAL));
        reservation.confirm(payment);

        // when
        List<MyReservationResponse> result = confirmedReservationApplicationService.findReservationsByMemberId(
                memberId);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
                    softAssertions.assertThat(result).hasSize(1);
                    softAssertions.assertThat(result.getFirst().theme()).isEqualTo("추리");
                    softAssertions.assertThat(result.getFirst().paymentKey()).isEqualTo("PAYMENT_KEY");
                }
        );
    }

    @Test
    void findReservations_ByMemberId_whenMemberIdNotFound_throwsMemberNotFoundException() {
        assertThatThrownBy(
                () -> confirmedReservationApplicationService.findReservationsByMemberId(999L))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("존재하지 않은 멤버입니다.");
    }
}
