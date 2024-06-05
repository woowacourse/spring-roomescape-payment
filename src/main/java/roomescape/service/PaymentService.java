package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.repository.PaymentRepository;
import roomescape.domain.repository.ReservationRepository;
import roomescape.infrastructure.payment.PaymentManager;
import roomescape.service.request.PaymentApproveDto;
import roomescape.service.response.PaymentDto;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentManager paymentManager;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository, PaymentManager paymentManager, ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentManager = paymentManager;
        this.reservationRepository = reservationRepository;
    }

    public PaymentDto save(Long reservationId, PaymentApproveDto paymentApproveDto) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("결제 승인 실패: 존재하지 않는 예약입니다. (id: %d)", reservationId)));
        validatePaymentNotExist(reservation);
        validatePaymentAmount(reservation, paymentApproveDto.amount());
        Payment payment = approveAndSavePayment(paymentApproveDto, reservation);

        return new PaymentDto(payment);
    }

    private Payment approveAndSavePayment(final PaymentApproveDto paymentApproveDto, final Reservation reservation) {
        PaymentDto paymentDto = paymentManager.approve(paymentApproveDto);
        Payment payment = new Payment(reservation, paymentDto.paymentKey(), paymentDto.orderId(), paymentDto.totalAmount());

        return paymentRepository.save(payment);
    }

    private void validatePaymentNotExist(Reservation reservation) {
        paymentRepository.findByReservationId(reservation.getId())
                .ifPresent((payment) -> {
                    throw new IllegalArgumentException(String.format("이미 결제가 존재하는 예약입니다. {reservationId: %d, paymentId: %d}",
                                    reservation.getId(), payment.getId()));
                });
    }

    private void validatePaymentAmount(Reservation reservation, Long amount) {
        Long themePrice = reservation.getTheme().getPrice();
        if (!themePrice.equals(amount)) {
            throw new IllegalArgumentException("테마 가격과 예약 금액이 일치하지 않습니다.");
        }
    }
}
