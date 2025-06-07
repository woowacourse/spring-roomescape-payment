package roomescape.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static roomescape.fixture.PaymentFixture.CREATE_PAYMENT_1;
import static roomescape.fixture.PendingPaymentFixture.CREATE_PENDING_PAYMENT_OF;
import static roomescape.fixture.ReservedFixture.CREATE_RESERVED_OF;
import static roomescape.fixture.ThemeFixture.CREATE_THEME_1;
import static roomescape.fixture.TimeSlotFixture.CREATE_TIME_SLOT_1;
import static roomescape.fixture.UserFixture.CREATE_USER_1;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.pendingpayment.PendingPayment;
import roomescape.domain.reservation.pendingpayment.PendingPaymentRepository;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.reservation.reserved.ReservedRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class PendingPaymentServiceTest {

    @Mock
    PaymentService paymentService;

    @Mock
    ReservedRepository reservedRepository;

    @Mock
    PendingPaymentRepository pendingPaymentRepository;

    @InjectMocks
    PendingPaymentService pendingPaymentService;

    @Nested
    @DisplayName("결제 대기 상태의 예약의 결제를 진행한다.")
    class CompletePayment {

        @Test
        @DisplayName("해당하는 ID의 결제 대기가 존재하지 않으면 예외를 던진다.")
        void completePayment_WhenPendingPaymentNotExists_ThenThrowException() {
            // given
            Long pendingPaymentId = 1L;
            PaymentInfo paymentInfo = new PaymentInfo("payment_key_1", "order_id_1", "order_name_1", 10000L);

            when(pendingPaymentRepository.findById(pendingPaymentId)).thenReturn(Optional.empty());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(
                            () -> pendingPaymentService.confirmPayment(pendingPaymentId, paymentInfo)).isInstanceOf(
                            NotFoundException.class).hasMessage("존재하지 않는 결제 대기입니다."),
                    () -> verify(pendingPaymentRepository).findById(pendingPaymentId)
            );
        }

        @Test
        @DisplayName("결제 대기 상태의 예약의 결제를 정상적으로 진행한다.")
        void completePayment() {
            // given
            Long pendingPaymentId = 1L;
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            User user = CREATE_USER_1();
            Payment payment = CREATE_PAYMENT_1();
            Reserved reserved = CREATE_RESERVED_OF(10L, user, date, timeSlot, theme, payment);

            PendingPayment pendingPayment = CREATE_PENDING_PAYMENT_OF(1L, user, date, timeSlot, theme);

            PaymentInfo paymentInfo = new PaymentInfo("payment_key_1", "order_id_1", "order_name_1", 10000L);

            when(pendingPaymentRepository.findById(pendingPaymentId)).thenReturn(Optional.of(pendingPayment));

            when(reservedRepository.save(Reserved.fromPendingPayment(pendingPayment))).thenReturn(reserved);

            // when
            pendingPaymentService.confirmPayment(pendingPaymentId, paymentInfo);

            // then
            assertAll(
                    () -> verify(paymentService).requestPayment(reserved, paymentInfo),
                    () -> verify(pendingPaymentRepository).findById(pendingPaymentId),
                    () -> verify(reservedRepository).save(any(Reserved.class)),
                    () -> verify(pendingPaymentRepository).deleteById(pendingPaymentId),
                    () -> verify(reservedRepository).save(any(Reserved.class))
            );
        }
    }
}
