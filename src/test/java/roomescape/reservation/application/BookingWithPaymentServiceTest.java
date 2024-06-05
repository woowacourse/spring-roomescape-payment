package roomescape.reservation.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import roomescape.payment.application.PaymentService;
import roomescape.payment.domain.ConfirmedPayment;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.event.ReservationSavedEvent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;

@RecordApplicationEvents
class BookingWithPaymentServiceTest extends ReservationServiceTest {
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
    void createWithPayment() {
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
    void create() {
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
