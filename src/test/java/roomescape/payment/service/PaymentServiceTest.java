package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.exception.BadArgumentRequestException;
import roomescape.member.domain.Member;
import roomescape.member.dto.MemberResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Schedule;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeResponse;
import roomescape.time.domain.ReservationTime;
import roomescape.time.dto.TimeResponse;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private static final PaymentRequest DEFAULT_REQUEST = new PaymentRequest(1L, "test_key", "test_order_id",
            BigDecimal.valueOf(1000L));
    private static final Member DEFAULT_MEMBER = new Member(1L, "브라운", "brown@abc.com");
    private static final ReservationTime DEFAULT_TIME = new ReservationTime(1L, LocalTime.of(19, 0));
    private static final Theme DEFAULT_THEME = new Theme(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg");
    private static final LocalDate DEFAULT_DATE = LocalDate.now().plusDays(7);
    private static final Schedule SCHEDULE = new Schedule(1L, DEFAULT_DATE, DEFAULT_TIME, DEFAULT_THEME);
    private static final Reservation DEFAULT_RESERVATION = new Reservation(
            1L, DEFAULT_MEMBER, DEFAULT_DATE, DEFAULT_TIME, DEFAULT_THEME);
    private static final Payment SAVED_PAYMENT = new Payment(
            1L, "test_key", BigDecimal.valueOf(1000L), DEFAULT_MEMBER, SCHEDULE);
    private static final PaymentResponse EXPECTED_RESPONSE = new PaymentResponse(
            1L, MemberResponse.from(DEFAULT_MEMBER), LocalDate.now().plusDays(7),
            TimeResponse.from(DEFAULT_TIME), ThemeResponse.from(DEFAULT_THEME), "test_key", BigDecimal.valueOf(1000L));

    @Mock
    private PaymentClient paymentClient;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private PaymentService paymentService;

    @DisplayName("결제를 생성할 수 있다.")
    @Test
    void createPaymentTest() {
        given(reservationRepository.findById(1L)).willReturn(Optional.of(DEFAULT_RESERVATION));
        given(paymentRepository.save(any())).willReturn(SAVED_PAYMENT);

        PaymentResponse actual = paymentService.createPayment(DEFAULT_REQUEST, 1L);

        assertThat(actual).isEqualTo(EXPECTED_RESPONSE);
    }

    @DisplayName("결제 생성 시, 해당 예약이 없다면 예외를 던진다.")
    @Test
    void createPaymentTest_whenReservationNotExist() {
        given(reservationRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.createPayment(DEFAULT_REQUEST, 1L))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("해당 예약이 존재하지 않습니다.");
    }

    @DisplayName("결제 생성 시, 예약 시간이 현재 시간 이전이라면 예외를 던진다.")
    @Test
    void createPaymentTest_whenReservationIsBefore() {
        Reservation pastReservation = new Reservation(
                1L, DEFAULT_MEMBER, LocalDate.now().minusDays(7), DEFAULT_TIME, DEFAULT_THEME);
        ;
        given(reservationRepository.findById(1L)).willReturn(Optional.of(pastReservation));

        assertThatThrownBy(() -> paymentService.createPayment(DEFAULT_REQUEST, 1L))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("결제하기 위해서 해당 예약은 현재 시간 이후여야 합니다.");
    }

    @DisplayName("결제 생성 시, 예약이 이미 결제된 경우 예외를 던진다.")
    @Test
    void createPaymentTest_whenReservationIsAlreadyPaid() {
        Reservation paidReservation = new Reservation(1L, DEFAULT_MEMBER, DEFAULT_DATE, DEFAULT_TIME, DEFAULT_THEME);
        paidReservation.completePaying();
        given(reservationRepository.findById(1L)).willReturn(Optional.of(paidReservation));

        assertThatThrownBy(() -> paymentService.createPayment(DEFAULT_REQUEST, 1L))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("해당 예약은 이미 결제 되었습니다.");
    }

    @DisplayName("결제 생성 시, 예약한 회원과 다른 회원이 결제하려고 할 경우 예외를 던진다.")
    @Test
    void createPaymentTest_whenMemberIsDifferent() {
        Reservation reservation = new Reservation(1L, DEFAULT_MEMBER, DEFAULT_DATE, DEFAULT_TIME, DEFAULT_THEME);
        given(reservationRepository.findById(1L)).willReturn(Optional.of(reservation));

        assertThatThrownBy(() -> paymentService.createPayment(DEFAULT_REQUEST, 2L))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("예약한 회원과 동일한 회원이 결제해야 합니다.");
    }
}
