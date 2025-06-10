package roomescape.reservation.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import roomescape.fixture.db.OrdersDbFixture;
import roomescape.fixture.db.ReservationDbFixture;
import roomescape.payment.domain.Orders;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.repository.ReservationRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ReservationPaymentServiceTest {

    @Autowired
    private OrdersDbFixture ordersDbFixture;

    @Autowired
    private ReservationDbFixture reservationDbFixture;

    @Autowired
    private ReservationPaymentService reservationPaymentService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void 결제가_되면_RESERVED_상태로_변경된다() {
        // given
        Orders orders = ordersDbFixture.create();
        String paymentKey = orders.getPaymentKey();

        Reservation pending = reservationDbFixture.pending();
        Long id = pending.getId();

        // when
        reservationPaymentService.paid(id, paymentKey);

        // then
        Reservation reservation = reservationRepository.findById(id).get();
        Orders orderResult = reservation.getOrders();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);
            softly.assertThat(orderResult.getOrderId()).isEqualTo(orders.getOrderId());
            softly.assertThat(orderResult.getPaymentKey()).isEqualTo(orders.getPaymentKey());
        });
    }

}
