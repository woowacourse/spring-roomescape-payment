package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt;

import java.time.LocalDate;
import java.time.LocalTime;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import roomescape.DBHelper;
import roomescape.auth.dto.LoginMember;
import roomescape.member.domain.Member;
import roomescape.member.dto.MemberResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTimeoutException;
import roomescape.payment.exception.TossPaymentException;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeResponse;

@Transactional
@DataJpaTest
@Import({TossConfirmationService.class, TossPaymentEventProcessor.class, ReservationPaymentCreator.class,
        NotPaidPaymentProcessor.class, PaymentCancellationService.class, DBHelper.class})
class TossConfirmationServiceTest {

    @Autowired
    private TossConfirmationService tossConfirmationService;

    @Autowired
    NotPaidPaymentProcessor notPaidPaymentProcessor;

    @Autowired
    private TossPaymentEventProcessor tossPaymentEventProcessor;

    @Autowired
    private ReservationPaymentCreator reservationPaymentCreator;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private TossRestClient tossRestClient;

    @MockitoBean
    private ReservationService reservationService;

    @Autowired
    DBHelper dbHelper;

    @DisplayName("예약과 결제를 성공적으로 처리한다")
    @Test
    void reserveAndPay_success() {
        // given
        LocalDate date = DEFAULT_DATE;
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());

        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        String paymentType = "CARD";
        ReservationPaymentRequest request = new ReservationPaymentRequest(
                date, theme.getId(), time.getId(),
                paymentKey, orderId, amount, paymentType);

        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, date, time, theme));
        ReservationResponse fakeReservationResponse = new ReservationResponse(
                reservation.getId(),
                date,
                new ReservationTimeResponse(time.getId(), time.getStartAt()),
                new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail()),
                new MemberResponse(member.getId(), member.getName(), member.getEmail(), member.getRole().name())
        );
        given(reservationService.registerReservation(any())).willReturn(fakeReservationResponse);

        TossPaymentResponse mockResponse = mock(TossPaymentResponse.class);
        given(mockResponse.method()).willReturn("CARD");
        given(mockResponse.cardNumber()).willReturn("1234-****-****-1234");
        given(mockResponse.cardApprovedNo()).willReturn("12345678");
        given(mockResponse.easyPayProvider()).willReturn(null);
        given(mockResponse.receiptUrl()).willReturn("https://receipt.url");

        given(tossRestClient.confirm(any(TossPaymentRequest.class)))
                .willReturn(mockResponse);

        // when
        tossConfirmationService.reserveAndPay(request, LoginMember.from(member));

        // then
        Payment payment = paymentRepository.findByPaymentKey(paymentKey).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
            softly.assertThat(payment.getMethod()).isEqualTo("CARD");
            softly.assertThat(payment.getCardNumber()).isEqualTo("1234-****-****-1234");
            softly.assertThat(payment.getCardApprovedNo()).isEqualTo("12345678");
            softly.assertThat(payment.getReceiptUrl()).isEqualTo("https://receipt.url");
        });
    }

    @DisplayName("예약 후 결제 승인 중 실패하면 TossPaymentException을 던진다")
    @Test
    void reserveAndPay_fail() {
        // given
        LocalDate date = DEFAULT_DATE;
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());

        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        String paymentType = "CARD";
        ReservationPaymentRequest request = new ReservationPaymentRequest(
                date, theme.getId(), time.getId(),
                paymentKey, orderId, amount, paymentType);

        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, date, time, theme));
        ReservationResponse fakeReservationResponse = new ReservationResponse(
                reservation.getId(),
                date,
                new ReservationTimeResponse(time.getId(), time.getStartAt()),
                new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail()),
                new MemberResponse(member.getId(), member.getName(), member.getEmail(), member.getRole().name())
        );
        given(reservationService.registerReservation(any())).willReturn(fakeReservationResponse);

        given(tossRestClient.confirm(any(TossPaymentRequest.class)))
                .willThrow(new TossPaymentException(HttpStatus.BAD_REQUEST, "결제 승인에 실패했습니다.", false));

        // when & then
        assertThatThrownBy(() -> tossConfirmationService.reserveAndPay(request, LoginMember.from(member)))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("결제 승인에 실패했습니다.");

        Payment payment = paymentRepository.findByPaymentKey(paymentKey).orElseThrow();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @DisplayName("예약 후 결제 승인 중 타임아웃이 발생하면 PaymentTimeoutException을 던진다")
    @Test
    void reserveAndPay_timeout() {
        // given
        LocalDate date = DEFAULT_DATE;
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());

        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        String paymentType = "CARD";
        ReservationPaymentRequest request = new ReservationPaymentRequest(
                date, theme.getId(), time.getId(),
                paymentKey, orderId, amount, paymentType);

        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, date, time, theme));
        ReservationResponse fakeReservationResponse = new ReservationResponse(
                reservation.getId(),
                date,
                new ReservationTimeResponse(time.getId(), time.getStartAt()),
                new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail()),
                new MemberResponse(member.getId(), member.getName(), member.getEmail(), member.getRole().name())
        );
        given(reservationService.registerReservation(any())).willReturn(fakeReservationResponse);

        given(tossRestClient.confirm(any(TossPaymentRequest.class)))
                .willThrow(new PaymentTimeoutException("결제 승인 시간이 초과되었습니다."));

        // when & then
        assertThatThrownBy(() -> tossConfirmationService.reserveAndPay(request, LoginMember.from(member)))
                .isInstanceOf(PaymentTimeoutException.class)
                .hasMessageContaining("결제 승인 시간이 초과되었습니다.");
    }

    @DisplayName("결제되지 않은 예약을 성공적으로 결제 승인한다")
    @Test
    void proceedPaymentForNotPaidReservation_success() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertNotPaidPayment(reservation);

        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        String paymentType = "CARD";
        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount, paymentType);

        TossPaymentResponse mockResponse = mock(TossPaymentResponse.class);
        given(mockResponse.method()).willReturn("CARD");
        given(mockResponse.cardNumber()).willReturn("1234-****-****-1234");
        given(mockResponse.cardApprovedNo()).willReturn("12345678");
        given(mockResponse.easyPayProvider()).willReturn(null);
        given(mockResponse.receiptUrl()).willReturn("https://receipt.url");

        given(tossRestClient.confirm(any(TossPaymentRequest.class)))
                .willReturn(mockResponse);

        // when
        tossConfirmationService.proceedPaymentForNotPaidReservation(
                reservation.getId(), request, LoginMember.from(member));

        // then
        Payment updatedPayment = paymentRepository.findByPaymentKey(paymentKey).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
            softly.assertThat(updatedPayment.getMethod()).isEqualTo("CARD");
            softly.assertThat(updatedPayment.getCardNumber()).isEqualTo("1234-****-****-1234");
            softly.assertThat(updatedPayment.getCardApprovedNo()).isEqualTo("12345678");
            softly.assertThat(updatedPayment.getReceiptUrl()).isEqualTo("https://receipt.url");
        });
    }

    @DisplayName("결제되지 않은 예약의 상태가 NOT_PAID가 아니면 TossPaymentException을 던진다")
    @Test
    void proceedPaymentForNotPaidReservation_invalidStatus() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertCompletedPayment(reservation);

        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        String paymentType = "CARD";
        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount, paymentType);

        // when & then
        assertThatThrownBy(() -> tossConfirmationService.proceedPaymentForNotPaidReservation(
                reservation.getId(), request, LoginMember.from(member)))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("결제를 시작할 수 있는 상태가 아닙니다");
    }

    @DisplayName("결제 상태가 PENDING이 아니면 TossPaymentException을 던진다")
    @Test
    void confirmPayment_invalidStatus() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertCompletedPayment(reservation);

        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        String paymentType = "CARD";
        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount, paymentType);

        // when & then
        assertThatThrownBy(() -> tossConfirmationService.proceedPaymentForNotPaidReservation(
                reservation.getId(), request, LoginMember.from(member)))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("결제를 시작할 수 있는 상태가 아닙니다, 현재 상태: 결제 완료");
    }

    @DisplayName("결제 상태를 COMPLETED로 갱신하고 상세 정보를 저장한다")
    @Test
    void updatePaymentInfoAfterConfirm_success() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertNotPaidPayment(reservation);

        TossPaymentResponse mockResponse = mock(TossPaymentResponse.class);
        given(mockResponse.method()).willReturn("CARD");
        given(mockResponse.cardNumber()).willReturn("1234-****-****-1234");
        given(mockResponse.cardApprovedNo()).willReturn("12345678");
        given(mockResponse.easyPayProvider()).willReturn(null);
        given(mockResponse.receiptUrl()).willReturn("https://receipt.url");

        // when
        payment.updateStatusTo(PaymentStatus.COMPLETED);
        payment.updateConfirmed(
                mockResponse.method(),
                mockResponse.cardNumber(),
                mockResponse.cardApprovedNo(),
                mockResponse.easyPayProvider(),
                mockResponse.receiptUrl()
        );
        paymentRepository.save(payment);

        // then
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
            softly.assertThat(updatedPayment.getMethod()).isEqualTo("CARD");
            softly.assertThat(updatedPayment.getCardNumber()).isEqualTo("1234-****-****-1234");
            softly.assertThat(updatedPayment.getCardApprovedNo()).isEqualTo("12345678");
            softly.assertThat(updatedPayment.getReceiptUrl()).isEqualTo("https://receipt.url");
        });
    }

    @DisplayName("Toss 응답에 따라 결제 상태를 COMPLETED로 설정한다")
    @Test
    void confirmToTossWithFallBack_success() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertNotPaidPayment(reservation);

        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        String paymentType = "CARD";
        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount, paymentType);

        TossPaymentResponse mockResponse = mock(TossPaymentResponse.class);
        given(mockResponse.method()).willReturn("CARD");
        given(mockResponse.cardNumber()).willReturn("1234-****-****-1234");
        given(mockResponse.cardApprovedNo()).willReturn("12345678");
        given(mockResponse.easyPayProvider()).willReturn(null);
        given(mockResponse.receiptUrl()).willReturn("https://receipt.url");

        given(tossRestClient.confirm(any(TossPaymentRequest.class)))
                .willReturn(mockResponse);

        // when
        tossConfirmationService.proceedPaymentForNotPaidReservation(
                reservation.getId(), request, LoginMember.from(member));

        // then
        Payment updatedPayment = paymentRepository.findByPaymentKey(paymentKey).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @DisplayName("Toss에서 예외 발생 시 결제 상태를 FAILED로 변경하고 예외를 던진다")
    @Test
    void confirmToTossWithFallBack_fail() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertNotPaidPayment(reservation);

        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        String paymentType = "CARD";
        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount, paymentType);

        given(tossRestClient.confirm(any(TossPaymentRequest.class)))
                .willThrow(new TossPaymentException(HttpStatus.BAD_REQUEST, "결제 승인에 실패했습니다.", false));

        // when & then
        assertThatThrownBy(() -> tossConfirmationService.proceedPaymentForNotPaidReservation(
                reservation.getId(), request, LoginMember.from(member)))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("결제 승인에 실패했습니다.");

        Payment updatedPayment = paymentRepository.findByPaymentKey(paymentKey).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }
}
