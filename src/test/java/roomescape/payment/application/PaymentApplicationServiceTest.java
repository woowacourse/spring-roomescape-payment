package roomescape.payment.application;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.config.TestConfig;
import roomescape.fixture.TestFixture;
import roomescape.member.domain.Member;
import roomescape.member.infrastructure.MemberRepository;
import roomescape.payment.application.client.PaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.exception.PaymentKeyDuplicatedException;
import roomescape.payment.infrastructure.PaymentRepository;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.infrastructure.ReservationRepository;
import roomescape.reservationslot.domain.ReservationSlot;
import roomescape.reservationslot.infrastructure.ReservationSlotRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.infrastructure.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.infrastructure.ThemeRepository;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(TestConfig.class)
class PaymentApplicationServiceTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationSlotRepository reservationSlotRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @MockitoBean
    private PaymentClient paymentClient;

    private PaymentApplicationService paymentApplicationService;

    @BeforeEach
    void setUp() {
        when(paymentClient.approvePayment(any())).thenAnswer(invocation -> {
            PaymentApproveRequest req = invocation.getArgument(0);
            return new PaymentApproveResponse(req.paymentKey(), req.orderId(), req.amount());
        });
        PaymentDataService paymentDataService = new PaymentDataService(paymentRepository);
        paymentApplicationService = new PaymentApplicationService(paymentDataService, paymentClient);
    }

    @Test
    void 정상적으로_결제_승인_후_결제_레코드가_생성된다() {
        // Given
        Member member = TestFixture.makeMember();
        memberRepository.save(member);
        Theme theme = new Theme("추리", "셜록 홈즈: 실종된 보석의 비밀", "sherlock_jewel.png");
        themeRepository.save(theme);
        ReservationTime reservationTime = TestFixture.makeReservationTime(LocalTime.of(10, 0));
        reservationTimeRepository.save(reservationTime);
        ReservationSlot reservationSlot = TestFixture.makeConfirmedReservation(LocalDate.now().plusDays(1), reservationTime, member, theme);
        reservationSlotRepository.save(reservationSlot);
        Reservation reservation = reservationRepository.findByReservationSlotIdAndMemberId(reservationSlot.getId(), member.getId()).get();

        PaymentApproveRequest paymentApproveRequest = new PaymentApproveRequest("testtest", "orderorder", 1000L);

        // When
        Payment actual = paymentApplicationService.approveReservationPayment(paymentApproveRequest, reservation.getId());

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getId()).isNotNull();
            softAssertions.assertThat(actual.getPaymentKey()).isEqualTo(paymentApproveRequest.paymentKey());
            softAssertions.assertThat(actual.getOrderId()).isEqualTo(paymentApproveRequest.orderId());
            softAssertions.assertThat(actual.getAmount()).isEqualTo(paymentApproveRequest.amount());
        });
    }

    @Test
    void 중복된_paymentKey는_저장될_수_없다() {
        // Given
        Member member = TestFixture.makeMember();
        memberRepository.save(member);
        Theme theme = new Theme("추리", "셜록 홈즈: 실종된 보석의 비밀", "sherlock_jewel.png");
        themeRepository.save(theme);
        ReservationTime reservationTime = TestFixture.makeReservationTime(LocalTime.of(10, 0));
        reservationTimeRepository.save(reservationTime);
        ReservationSlot reservationSlot = TestFixture.makeConfirmedReservation(LocalDate.now().plusDays(1), reservationTime, member, theme);
        reservationSlotRepository.save(reservationSlot);
        Reservation reservation = reservationRepository.findByReservationSlotIdAndMemberId(reservationSlot.getId(), member.getId()).get();

        PaymentApproveRequest paymentApproveRequest1 = new PaymentApproveRequest("testtest", "orderorder", 1000L);
        PaymentApproveRequest paymentApproveRequest2 = new PaymentApproveRequest("testtest", "orderorder", 1000L);

        paymentApplicationService.approveReservationPayment(paymentApproveRequest1, reservation.getId());

        // When & Then
        assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequest2, reservation.getId()))
                .isInstanceOf(PaymentKeyDuplicatedException.class)
                .hasMessage("중복된 paymentKey입니다.");
    }

    @Test
    void paymentKey는_null이나_빈_값이_될_수_없다() {
        // Given
        Member member = TestFixture.makeMember();
        memberRepository.save(member);
        Theme theme = new Theme("추리", "셜록 홈즈: 실종된 보석의 비밀", "sherlock_jewel.png");
        themeRepository.save(theme);
        ReservationTime reservationTime = TestFixture.makeReservationTime(LocalTime.of(10, 0));
        reservationTimeRepository.save(reservationTime);
        ReservationSlot reservationSlot = TestFixture.makeConfirmedReservation(LocalDate.now().plusDays(1), reservationTime, member, theme);
        reservationSlotRepository.save(reservationSlot);
        Reservation reservation = reservationRepository.findByReservationSlotIdAndMemberId(reservationSlot.getId(), member.getId()).get();

        PaymentApproveRequest paymentApproveRequestWithPaymentKeyNull = new PaymentApproveRequest(null, "orderorder", 1000L);
        PaymentApproveRequest paymentApproveRequestWithPaymentKeyBlank = new PaymentApproveRequest("", "orderorder", 1000L);

        // When & Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithPaymentKeyNull, reservation.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("키가 올바르지 않습니다.");
            softAssertions.assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithPaymentKeyBlank, reservation.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("키가 올바르지 않습니다.");
        });
    }

    @Test
    void 주문_번호는_null이나_빈_값이_될_수_없다() {
        // Given
        Member member = TestFixture.makeMember();
        memberRepository.save(member);
        Theme theme = new Theme("추리", "셜록 홈즈: 실종된 보석의 비밀", "sherlock_jewel.png");
        themeRepository.save(theme);
        ReservationTime reservationTime = TestFixture.makeReservationTime(LocalTime.of(10, 0));
        reservationTimeRepository.save(reservationTime);
        ReservationSlot reservationSlot = TestFixture.makeConfirmedReservation(LocalDate.now().plusDays(1), reservationTime, member, theme);
        reservationSlotRepository.save(reservationSlot);
        Reservation reservation = reservationRepository.findByReservationSlotIdAndMemberId(reservationSlot.getId(), member.getId()).get();

        PaymentApproveRequest paymentApproveRequestWithOrderIdNull = new PaymentApproveRequest("testtest", null, 1000L);
        PaymentApproveRequest paymentApproveRequestWithOrderIdBlank = new PaymentApproveRequest("testtest", "", 1000L);

        // When & Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithOrderIdNull, reservation.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("주문 번호가 올바르지 않습니다.");
            softAssertions.assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithOrderIdBlank, reservation.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("주문 번호가 올바르지 않습니다.");
        });
    }

    @Test
    void 결제_금액은_null이_될_수_없다() {
        // Given
        Member member = TestFixture.makeMember();
        memberRepository.save(member);
        Theme theme = new Theme("추리", "셜록 홈즈: 실종된 보석의 비밀", "sherlock_jewel.png");
        themeRepository.save(theme);
        ReservationTime reservationTime = TestFixture.makeReservationTime(LocalTime.of(10, 0));
        reservationTimeRepository.save(reservationTime);
        ReservationSlot reservationSlot = TestFixture.makeConfirmedReservation(LocalDate.now().plusDays(1), reservationTime, member, theme);
        reservationSlotRepository.save(reservationSlot);
        Reservation reservation = reservationRepository.findByReservationSlotIdAndMemberId(reservationSlot.getId(), member.getId()).get();

        PaymentApproveRequest paymentApproveRequestWithAmountNull = new PaymentApproveRequest("testtest", "orderorder", null);

        // When & Then
        assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithAmountNull, reservation.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액은 null이 될 수 없습니다.");
    }

    @Test
    void 결제_금액은_최소_결제_금액_이상이어야_한다() {
        // Given
        Member member = TestFixture.makeMember();
        memberRepository.save(member);
        Theme theme = new Theme("추리", "셜록 홈즈: 실종된 보석의 비밀", "sherlock_jewel.png");
        themeRepository.save(theme);
        ReservationTime reservationTime = TestFixture.makeReservationTime(LocalTime.of(10, 0));
        reservationTimeRepository.save(reservationTime);
        ReservationSlot reservationSlot = TestFixture.makeConfirmedReservation(LocalDate.now().plusDays(1), reservationTime, member, theme);
        reservationSlotRepository.save(reservationSlot);
        Reservation reservation = reservationRepository.findByReservationSlotIdAndMemberId(reservationSlot.getId(), member.getId()).get();

        PaymentApproveRequest paymentApproveRequestWithAmountUnderUnitPrice = new PaymentApproveRequest("testtest", "orderorder", 900L);

        // When & Then
        assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithAmountUnderUnitPrice, reservation.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액이 잘못되었습니다.");
    }

    @Test
    void 결제_금액은_최소_결제_금액으로_나누어_떨어져야_한다() {
        // Given
        Member member = TestFixture.makeMember();
        memberRepository.save(member);
        Theme theme = new Theme("추리", "셜록 홈즈: 실종된 보석의 비밀", "sherlock_jewel.png");
        themeRepository.save(theme);
        ReservationTime reservationTime = TestFixture.makeReservationTime(LocalTime.of(10, 0));
        reservationTimeRepository.save(reservationTime);
        ReservationSlot reservationSlot = TestFixture.makeConfirmedReservation(LocalDate.now().plusDays(1), reservationTime, member, theme);
        reservationSlotRepository.save(reservationSlot);
        Reservation reservation = reservationRepository.findByReservationSlotIdAndMemberId(reservationSlot.getId(), member.getId()).get();

        PaymentApproveRequest paymentApproveRequestWithAmountUnderUnitPrice = new PaymentApproveRequest("testtest", "orderorder", 999L);

        // When & Then
        assertThatThrownBy(() -> paymentApplicationService.approveReservationPayment(paymentApproveRequestWithAmountUnderUnitPrice, reservation.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액이 잘못되었습니다.");
    }
}
