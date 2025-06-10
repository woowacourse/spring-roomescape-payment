package roomescape.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.NotFoundException;
import roomescape.logging.aspect.Loggable;
import roomescape.payment.domain.Orders;
import roomescape.payment.repository.OrdersRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.repository.ReservationRepository;

@Service
@RequiredArgsConstructor
public class ReservationPaymentService {

    private final ReservationRepository reservationRepository;
    private final OrdersRepository ordersRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void paid(Long reservationId, String paymentKey) {
        Reservation reservation = reservationRepository.findByIdAndStatus(reservationId,
                ReservationStatus.PENDING).orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));
        Orders orders = getOrders(paymentKey);

        orders.success();
        reservation.paid(orders);
    }

    @Loggable
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failedPayment(Long reservationId, String paymentKey) {
        Reservation reservation = reservationRepository.findByIdAndStatus(reservationId,
                ReservationStatus.PENDING).orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));
        Orders orders = getOrders(paymentKey);

        orders.failed();
        reservation.updateOrders(orders);
    }

    private Orders getOrders(String paymentKey) {
        return ordersRepository.findByPaymentKey(paymentKey).orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
    }
}
