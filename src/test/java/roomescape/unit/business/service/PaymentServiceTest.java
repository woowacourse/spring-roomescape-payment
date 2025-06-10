package roomescape.unit.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.auth.LoginInfo;
import roomescape.business.model.entity.Member;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.entity.TimeSlot;
import roomescape.business.model.vo.Id;
import roomescape.business.model.vo.PaymentStatus;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.PaymentService;
import roomescape.business.service.ReservationService;
import roomescape.exception.payment.PaymentIntegrityViolationException;
import roomescape.exception.payment.PaymentNotFoundException;
import roomescape.infrastructure.PaymentRepository;
import roomescape.presentation.dto.request.PaymentAndReservationRequest;
import roomescape.presentation.dto.request.ReservationRequest;
import roomescape.presentation.dto.response.PaymentResponse;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private final PaymentService paymentService;
    private final ReservationService reservationService = mock(ReservationService.class);
    private final PaymentRepository paymentRepository = mock(PaymentRepository.class);

    public PaymentServiceTest() {
        this.paymentService = new PaymentService(reservationService, paymentRepository);
    }

    @Test
    @DisplayName("결제와 예약을 생성하고 PaymentResponse를 반환한다")
    void 결제를_생성한다() {
        // given
        Member member = Member.restore("memberId1", UserRole.USER.name(), "name", "email1@domain.com", "password1");
        Theme theme = Theme.restore("themeId1", "theme1", "description1", "thumbnail1", 1000L);
        TimeSlot timeSlot = TimeSlot.restore("timeSlotId1", LocalTime.of(9, 0));
        LocalDate date = LocalDate.now().plusDays(1);
        Reservation reservation = Reservation.restore("reservationId1", member, date, timeSlot, theme);

        PaymentAndReservationRequest request = new PaymentAndReservationRequest(date, timeSlot.getId().id(),
                theme.getId().id());
        ReservationRequest reservationRequest = new ReservationRequest(request.themeId(), request.timeId(),
                request.date());
        LoginInfo loginInfo = new LoginInfo(member.getId().id(), member.getRole());
        given(reservationService.createReservation(loginInfo, reservationRequest)).willReturn(reservation);

        Payment payment = Payment.restore("paymentId1", "paymentKey1", theme.getPrice(), PaymentStatus.IN_PROGRESS,
                reservation);
        given(paymentRepository.save(any(Payment.class))).willReturn(payment);

        // when
        PaymentResponse result = paymentService.createPaymentAndReservation(loginInfo, request);

        // then
        assertThat(result.id()).isEqualTo(payment.getId().value());
        assertThat(result.amount()).isEqualTo(theme.getPrice());
        verify(reservationService).createReservation(loginInfo, reservationRequest);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void 결제_금액이_일치하지_않으면_예외가_발생한다() {
        // given
        String paymentId = "paymentId1";
        String paymentKey = "paymentKey1";
        Long correctAmount = 1000L;
        Long wrongAmount = 2000L;

        Reservation reservation = Reservation.restore("reservationId1", null, null, null, null);
        Payment payment = Payment.restore(paymentId, null, correctAmount, PaymentStatus.IN_PROGRESS, reservation);

        given(paymentRepository.findById(Id.create(paymentId))).willReturn(Optional.of(payment));

        // when & then
        assertThatThrownBy(
                () -> paymentService.completePayment(paymentId, paymentKey, wrongAmount))
                .isInstanceOf(PaymentIntegrityViolationException.class);
    }

    @Test
    void 결제가_존재하지_않으면_예외가_발생한다() {
        // given
        String paymentId = "paymentId1";
        String paymentKey = "paymentKey1";
        Long amount = 1000L;
        given(paymentRepository.findById(Id.create(paymentId))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                () -> paymentService.completePayment(paymentId, paymentKey, amount))
                .isInstanceOf(PaymentNotFoundException.class);
    }
}
