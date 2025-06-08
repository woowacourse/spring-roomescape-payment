package roomescape.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.client.PaymentClient;
import roomescape.client.dto.PaymentConfirmResultDto;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentResponseDto;
import roomescape.exception.NotFoundException;
import roomescape.repository.JpaPaymentRepository;
import roomescape.repository.JpaReservationRepository;
import roomescape.service.dto.PaymentConfirmDto;

@Service
public class PaymentCommandService {

    private final PaymentClient paymentClient;
    private final JpaPaymentRepository paymentRepository;
    private final JpaReservationRepository reservationRepository;

    public PaymentCommandService(
            PaymentClient paymentClient,
            JpaPaymentRepository paymentRepository,
            JpaReservationRepository reservationRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(propagation = Propagation.NEVER)
    public PaymentResponseDto confirmPayment(Long reservationId, PaymentConfirmDto confirmDto) {
        PaymentConfirmResultDto confirmResult = paymentClient.confirmPayment(confirmDto);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("해당하는 예약 정보를 찾을 수 없습니다."));
        Payment payment = paymentRepository.save(confirmResult.toPaymentEntity(reservation));
        return new PaymentResponseDto(payment.getPaymentKey(), payment.getOrderId(), payment.getTotalAmount());
    }
}
