package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static roomescape.fixture.PaymentFixture.CREATE_PAYMENT_OF;
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
import org.springframework.context.ApplicationEventPublisher;
import roomescape.application.event.PaymentRequestedEvent;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;


    @Nested
    @DisplayName("결제를 요청한다.")
    class RequestPayment {

        @Test
        @DisplayName("결제 요청 이벤트를 발행한다.")
        void requestPayment() {
            // given
            User user = CREATE_USER_1();
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            LocalDate date = LocalDate.now().plusDays(1);

            Reserved reserved = CREATE_RESERVED_OF(1L, user, date, timeSlot, theme);
            PaymentInfo paymentInfo = new PaymentInfo("payment_key_1", "order_id_1", "order_name_1", 10000L);
            Payment payment = CREATE_PAYMENT_OF(1L, paymentInfo);

            when(paymentRepository.save(Payment.register(
                    paymentInfo.paymentKey(),
                    paymentInfo.orderId(),
                    paymentInfo.orderName(),
                    paymentInfo.amount()
            ))).thenReturn(payment);

            // when
            paymentService.requestPayment(reserved, paymentInfo);

            // then
            assertAll(
                    () -> assertThat(reserved.getPayment()).isEqualTo(payment),
                    () -> verify(paymentRepository).save(any(Payment.class)),
                    () -> verify(eventPublisher).publishEvent(any(PaymentRequestedEvent.class))
            );
        }
    }

    @Nested
    @DisplayName("결제 정보의 상태를 성공으로 변경한다.")
    class CompletePayment {

        @Test
        @DisplayName("해당하는 ID의 결제 정보가 없으면 예외가 발생한다.")
        void completePayment_WhenPaymentNotExists_ThenThrowException() {
            // given
            Long paymentId = 9000L;

            when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> paymentService.completePayment(paymentId))
                            .isInstanceOf(NotFoundException.class)
                            .hasMessage("존재하지 않는 결제 정보입니다."),
                    () -> verify(paymentRepository).findById(paymentId)
            );
        }

        @Test
        @DisplayName("결제 정보의 상태를 정상적으로 성공으로 변경한다.")
        void completePayment() {
            // given
            Long paymentId = 1L;
            Payment payment = CREATE_PAYMENT_OF(paymentId);

            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

            // when
            paymentService.completePayment(paymentId);

            // then
            assertAll(
                    () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS),
                    () -> verify(paymentRepository).findById(paymentId)
            );
        }
    }

    @Nested
    @DisplayName("결제 정보의 상태를 실패로 변경한다.")
    class RejectPayment {

        @Test
        @DisplayName("해당하는 ID의 결제 정보가 없으면 예외가 발생한다.")
        void rejectPayment_WhenPaymentNotExists_ThenThrowException() {
            // given
            Long paymentId = 9000L;

            when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> paymentService.rejectPayment(paymentId))
                            .isInstanceOf(NotFoundException.class)
                            .hasMessage("존재하지 않는 결제 정보입니다."),
                    () -> verify(paymentRepository).findById(paymentId)
            );
        }

        @Test
        @DisplayName("결제 정보의 상태를 정상적으로 실패로 변경한다.")
        void rejectPayment() {
            // given
            Long paymentId = 1L;
            Payment payment = CREATE_PAYMENT_OF(paymentId);

            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

            // when
            paymentService.rejectPayment(paymentId);

            // then
            assertAll(
                    () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED),
                    () -> verify(paymentRepository).findById(paymentId)
            );
        }
    }

}
