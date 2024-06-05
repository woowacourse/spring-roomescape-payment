package roomescape.reservation.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import roomescape.payment.application.PaymentService;
import roomescape.payment.domain.ConfirmedPayment;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.event.ReservationFailedEvent;
import roomescape.reservation.event.ReservationSavedEvent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;

class BookingWithPaymentServiceTest {

    @Nested
    @RecordApplicationEvents
    class BookingSuccessTest extends ReservationServiceTest {
        @Autowired
        private BookingManageService bookingManageService;

        @Autowired
        private PaymentService paymentService;

        @Autowired
        private PaymentRepository paymentRepository;

        @Autowired
        private ApplicationEvents events;

        @Test
        @DisplayName("예약을 생성하고 결제 정보를 담은 이벤트를 발행한다.")
        void createBookingWithPayment() {
            // given
            Reservation reservation = MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, BOOKING);
            ConfirmedPayment confirmedPayment = new ConfirmedPayment("paymentKey", "orderId", 10);

            // when
            Reservation createdReservation = bookingManageService.createWithPayment(reservation, confirmedPayment);

            // then
            long count = events.stream(ReservationSavedEvent.class).count();
            assertSoftly(softly -> {
                softly.assertThat(createdReservation.getId()).isNotNull();
                softly.assertThat(count).isEqualTo(1);
            });
        }

        @Test
        @DisplayName("발행된 이벤트의 예약과 결제 정보로 결제 내역을 생성한다.")
        void createPayment() {
            // given
            Reservation reservation = bookingManageService.create(MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, BOOKING));

            ConfirmedPayment confirmedPayment = new ConfirmedPayment("paymentKey", "orderId", 10);
            ReservationSavedEvent reservationSavedEvent = new ReservationSavedEvent(reservation, confirmedPayment);

            // when
            paymentService.create(reservationSavedEvent);

            // then
            List<Payment> payments = paymentRepository.findAll();
            assertThat(payments).hasSize(1);
        }
    }

    @Nested
    @RecordApplicationEvents
    class BookingFailTest extends ReservationServiceTest {
        @SpyBean
        private PaymentService paymentService;

        @SpyBean
        private BookingManageService bookingManageService;

        @Autowired
        private ReservationRepository reservationRepository;

        @Autowired
        private ApplicationEvents events;

        @Test
        @DisplayName("예약 생성에 실패하면 PG 결제 취소 API에 취소 요청을 한다.")
        void cancelCasedByRollBackWhenReservationCreateFailed() {
            // given
            BDDMockito.willThrow(RuntimeException.class)
                    .given(bookingManageService)
                    .create(any());

            Reservation reservation = MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, BOOKING);
            ConfirmedPayment confirmedPayment = new ConfirmedPayment("paymentKey", "orderId", 10);

            // when & then
            assertThatThrownBy(() -> bookingManageService.createWithPayment(reservation, confirmedPayment))
                    .isInstanceOf(RuntimeException.class);

            long count = events.stream(ReservationFailedEvent.class).count();
            assertThat(count).isEqualTo(1);
            verify(paymentService, times(1)).cancelCasedByRollBack(any());
        }

        @Test
        @DisplayName("결제 내역 생성에 실패하면 PG 결제 취소 API에 취소 요청을 한다.")
        void cancelCasedByRollBackWhenPaymentCreateFailed() {
            // given
            BDDMockito.willThrow(RuntimeException.class)
                    .given(paymentService)
                    .create(any());

            Reservation reservation = MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, BOOKING);
            ConfirmedPayment confirmedPayment = new ConfirmedPayment("paymentKey", "orderId", 10);

            // when & then
            assertSoftly(softly -> {
                softly.assertThatThrownBy(() -> bookingManageService.createWithPayment(reservation, confirmedPayment))
                        .isInstanceOf(RuntimeException.class);

                long count = events.stream(ReservationFailedEvent.class).count();
                softly.assertThat(count).isEqualTo(1);
            });

            verify(paymentService, times(1)).cancelCasedByRollBack(any());
        }

        @Test
        @DisplayName("결제 내역 생성에 실패하면 예약 생성도 롤백된다.")
        void rollBackCreatedReservationWhenPaymentCreateFailed() {
            // given
            BDDMockito.willThrow(RuntimeException.class)
                    .given(paymentService)
                    .create(any());

            Reservation reservation = MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, BOOKING);
            ConfirmedPayment confirmedPayment = new ConfirmedPayment("paymentKey", "orderId", 10);

            // when & then
            assertSoftly(softly -> {
                softly.assertThatThrownBy(() -> bookingManageService.createWithPayment(reservation, confirmedPayment))
                        .isInstanceOf(RuntimeException.class);
                softly.assertThat(reservationRepository.findAll()).hasSize(0);
            });
        }
    }
}
