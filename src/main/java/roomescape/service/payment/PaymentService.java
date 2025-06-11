package roomescape.service.payment;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.response.PaymentSuccessResponse;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentApproveClient paymentApproveClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void approveAndSave(String paymentKey, String orderId, int amount, Long reservationId) {
        final PaymentSuccessResponse response = paymentApproveClient.approvePayment(
                paymentKey,
                orderId,
                amount
        );
        paymentRepository.save(new Payment(reservationId, response.paymentKey(), response.totalAmount()));
    }

    public Map<Reservation, Payment> getPaymentMapByReservations(List<Reservation> reservations) {
        final Map<Long, Payment> paymentById = paymentRepository
                .findByReservationIdIn(reservations.stream().map(Reservation::getId).toList())
                .stream()
                .collect(Collectors.toMap(Payment::getReservationId, Function.identity()));

        return reservations.stream()
                .filter(reservation -> paymentById.containsKey(reservation.getId()))
                .collect(Collectors.toUnmodifiableMap(
                        Function.identity(),
                        reservation -> paymentById.get(reservation.getId())
                ));
    }
}
