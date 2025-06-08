package roomescape.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static roomescape.fixture.TestFixture.FUTURE_DATE;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import roomescape.member.infrastructure.MemberRepository;
import roomescape.payment.application.dto.PaymentGatewayRequest;
import roomescape.payment.application.dto.PaymentGatewayResponse;
import roomescape.payment.application.infrastructure.PaymentRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentGateway;
import roomescape.payment.domain.PaymentType;
import roomescape.payment.exception.PaymentForbiddenException;
import roomescape.payment.presentation.dto.request.TossPaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;
import roomescape.reservation.application.ConfirmedReservationApplicationService;
import roomescape.reservation.application.ReservationDataService;
import roomescape.reservation.application.dto.request.ConfirmedReservationCreateRequest;
import roomescape.reservation.application.event.PaymentApprovedEvent;
import roomescape.reservation.application.event.TestEventPublisher;
import roomescape.reservation.infrastructure.ReservationRepository;
import roomescape.reservationslot.application.ReservationSlotDataService;
import roomescape.reservationslot.infrastructure.ReservationSlotRepository;
import roomescape.reservationtime.application.ReservationTimeDataService;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.infrastructure.ReservationTimeRepository;
import roomescape.theme.application.ThemeDataService;
import roomescape.theme.infrastructure.ThemeRepository;

@DataJpaTest
@Import(TestConfig.class)
class PaymentServiceTest {

    private static final LocalDateTime afterOneHour = TestFixture.makeTimeAfterOneHour();
    private static final String ORDER_ID = "ORDER_ID";
    private static final String PAYMENT_KEY = "PAYMENT_KEY";

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

    @Autowired
    private TestEventPublisher eventPublisher;

    @PersistenceContext
    private EntityManager entityManager;

    @MockitoBean
    private PaymentGateway paymentGateway;

    private PaymentService paymentService;
    private Long reservationId;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentGateway, paymentRepository, eventPublisher);
        ReservationSlotDataService reservationSlotDataService = new ReservationSlotDataService(
                reservationSlotRepository);
        ReservationTimeDataService reservationTimeDataService = new ReservationTimeDataService(
                reservationTimeRepository, reservationSlotDataService);
        ThemeDataService themeDataService = new ThemeDataService(themeRepository);
        MemberDataService memberDataService = new MemberDataService(memberRepository);
        ReservationDataService reservationDataService = new ReservationDataService(reservationRepository);
        ConfirmedReservationApplicationService confirmedReservationApplicationService = new ConfirmedReservationApplicationService(
                reservationSlotDataService,
                reservationTimeDataService, themeDataService, memberDataService, reservationDataService,
                eventPublisher);
        Long timeId = reservationTimeRepository.save(new ReservationTime(LocalTime.of(9, 0))).getId();
        Long themeId = themeRepository.save(TestFixture.makeTheme()).getId();
        Long memberId = memberRepository.save(TestFixture.makeMember()).getId();
        reservationId = confirmedReservationApplicationService.create(
                new ConfirmedReservationCreateRequest(FUTURE_DATE, timeId, themeId, memberId,
                        afterOneHour, null)).id();
        eventPublisher.clear();
    }

    @Test
    void approvePayment_whenValidRequest_returnDto() {
        // given
        long amount = 5000L;
        TossPaymentApproveRequest tossPaymentApproveRequest = new TossPaymentApproveRequest(PAYMENT_KEY, ORDER_ID, amount,
                reservationId);
        PaymentGatewayResponse paymentApproveResponse = new PaymentGatewayResponse(PAYMENT_KEY, ORDER_ID,
                amount);
        when(paymentGateway.approvePayment(any(PaymentGatewayRequest.class))).thenReturn(paymentApproveResponse);
        PaymentApproveResponse expected = new PaymentApproveResponse(ORDER_ID, amount);

        // when
        PaymentApproveResponse actual = paymentService.approvePayment(tossPaymentApproveRequest);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isEqualTo(expected);
            softAssertions.assertThat(eventPublisher.hasEvent(PaymentApprovedEvent.class)).isTrue();
            softAssertions.assertThat(eventPublisher.getEventsOfType(PaymentApprovedEvent.class)).hasSize(1);
        });
    }

    @Test
    void approvePayment_whenPaymentFailed_throwException() {
        // given
        long amount = 5000L;
        TossPaymentApproveRequest tossPaymentApproveRequest = new TossPaymentApproveRequest(PAYMENT_KEY, ORDER_ID, amount,
                reservationId);
        when(paymentGateway.approvePayment(any(PaymentGatewayRequest.class))).thenThrow(
                PaymentForbiddenException.class);

        // when
        Assertions.assertThatThrownBy(() -> paymentService.approvePayment(tossPaymentApproveRequest))
                .isInstanceOf(PaymentForbiddenException.class);
    }

    @Test
    void save() {
        // given
        long amount = 5000L;
        PaymentType paymentType = PaymentType.NORMAL;
        Payment payment = new Payment(PAYMENT_KEY, ORDER_ID, amount, paymentType);

        // when
        Payment savedPayment = paymentService.save(payment);
        entityManager.flush();
        entityManager.clear();

        // then
        Long savedPaymentId = savedPayment.getId();
        assertThat(savedPaymentId).isNotNull();
        Payment foundPayment = paymentRepository.findById(savedPaymentId).orElseThrow();
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(foundPayment.getPaymentKey()).isEqualTo(PAYMENT_KEY);
            softAssertions.assertThat(foundPayment.getOrderId()).isEqualTo(ORDER_ID);
            softAssertions.assertThat(foundPayment.getAmount()).isEqualTo(amount);
            softAssertions.assertThat(foundPayment.getPaymentType()).isEqualTo(paymentType);
        });
    }
}
