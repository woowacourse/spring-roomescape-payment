package roomescape.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static roomescape.fixture.MemberFixture.DEFAULT_MEMBER;
import static roomescape.fixture.PaymentFixture.INVALID_PAYMENT_INFO;
import static roomescape.fixture.PaymentFixture.PAYMENT_INFO;
import static roomescape.fixture.PaymentFixture.PAYMENT_REQUEST;
import static roomescape.fixture.PaymentFixture.PAYMENT_RESPONSE;
import static roomescape.fixture.ReservationTimeFixture.DEFAULT_RESERVATION_TIME;
import static roomescape.fixture.ThemeFixture.DEFAULT_THEME;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import roomescape.application.service.ReservationApplicationService;
import roomescape.auth.dto.LoginMemberResponse;
import roomescape.exception.PaymentException;
import roomescape.exception.response.UserPaymentExceptionResponse;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationFixture;
import roomescape.member.domain.LoginMember;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.api.PaymentClient;
import roomescape.payment.dto.CancelReason;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationPaymentResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.repository.ReservationTimeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Sql(value = "/clear.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class ReservationApplicationServiceTest {

    @MockBean
    private PaymentClient paymentClient;
    @Autowired
    private ReservationApplicationService reservationApplicationService;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    private final LoginMember loginMember = MemberFixture.DEFAULT_LOGIN_MEMBER;

    @BeforeEach
    void initData() {
        themeRepository.save(DEFAULT_THEME);
        reservationTimeRepository.save(DEFAULT_RESERVATION_TIME);
        memberRepository.save(DEFAULT_MEMBER);
    }

    @DisplayName("결제에 성공할 경우 예약에 성공한다.")
    @Test
    void saveReservationPayment() {
        ReservationPaymentRequest request = new ReservationPaymentRequest(
                LocalDate.now().plusDays(1),
                DEFAULT_THEME.getId(),
                DEFAULT_RESERVATION_TIME.getId(),
                PAYMENT_REQUEST.paymentKey(),
                PAYMENT_REQUEST.orderId(),
                PAYMENT_REQUEST.amount());
        ReservationPaymentResponse response = new ReservationPaymentResponse(
                new ReservationResponse(
                        1L,
                        LocalDate.now().plusDays(1),
                        ReservationTimeResponse.from(DEFAULT_RESERVATION_TIME),
                        ThemeResponse.from(DEFAULT_THEME),
                        new LoginMemberResponse(loginMember.getId(), loginMember.getName())),
                PAYMENT_RESPONSE);

        Mockito.when(paymentClient.payment(PAYMENT_REQUEST)).thenReturn(PAYMENT_INFO);

        assertThat(reservationApplicationService.saveReservationPayment(loginMember, request)).isEqualTo(response);
    }

    @DisplayName("결제에 실패할 경우 예약에 실패한다.")
    @Test
    void failSaveReservationPayment() {
        PaymentRequest invalidPaymentRequest = new PaymentRequest("invalidPaymentKey", "invalidOrderId", BigDecimal.valueOf(1000));
        ReservationPaymentRequest request = new ReservationPaymentRequest(
                LocalDate.now().plusDays(1),
                DEFAULT_THEME.getId(),
                DEFAULT_RESERVATION_TIME.getId(),
                invalidPaymentRequest.paymentKey(),
                invalidPaymentRequest.orderId(),
                invalidPaymentRequest.amount());

        Mockito.when(paymentClient.payment(invalidPaymentRequest))
                .thenThrow(new PaymentException(UserPaymentExceptionResponse.of("INVALID_PAYMENT_KEY", "올바르지 않은 PaymentKey 입니다.")));

        assertThatThrownBy(() -> reservationApplicationService.saveReservationPayment(loginMember, request))
                .isInstanceOf(PaymentException.class);
    }

    @DisplayName("결제 취소에 성공할 경우 예약, 결제 내역 삭제에 성공한다.")
    @Test
    void cancelReservationPayment() {
        Reservation reservation = ReservationFixture.ReservationOfDate(LocalDate.now().plusDays(1));
        Reservation savedReservation = reservationRepository.save(reservation);
        paymentRepository.save(new Payment(savedReservation, PAYMENT_INFO));

        Mockito.when(paymentClient.cancel(PAYMENT_REQUEST.paymentKey(), new CancelReason("관리자 권한 취소")))
                .thenReturn(PAYMENT_INFO);

        assertThatCode(() -> reservationApplicationService.cancelReservationPayment(savedReservation.getId()))
                .doesNotThrowAnyException();
    }

    @DisplayName("결제 취소에 실패할 경우 예약, 결제 내역 삭제에 실패한다.")
    @Test
    void failCancelReservationPayment() {
        Reservation reservation = ReservationFixture.ReservationOfDate(LocalDate.now().plusDays(1));
        Reservation savedReservation = reservationRepository.save(reservation);
        paymentRepository.save(new Payment(savedReservation, INVALID_PAYMENT_INFO));

        Mockito.when(paymentClient.cancel(INVALID_PAYMENT_INFO.paymentKey(), new CancelReason("관리자 권한 취소")))
                .thenThrow(new PaymentException(UserPaymentExceptionResponse.of("INVALID_PAYMENT_KEY", "올바르지 않은 PaymentKey 입니다.")));

        assertThatThrownBy(() -> reservationApplicationService.cancelReservationPayment(savedReservation.getId()))
                .isInstanceOf(PaymentException.class);
    }
}
