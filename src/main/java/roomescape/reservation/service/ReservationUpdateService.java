package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.BadArgumentRequestException;
import roomescape.reservation.domain.PaymentStatus;
import roomescape.reservation.repository.ReservationRepository;

@Service
public class ReservationUpdateService {
    private final ReservationRepository reservationRepository;

    public ReservationUpdateService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void updateReservationPaymentStatus(Long id, PaymentStatus paymentStatus) {
        reservationRepository.findById(id)
                .ifPresentOrElse(
                        reservation -> reservation.updatePaymentStatus(paymentStatus),
                        () -> new BadArgumentRequestException("해당 예약을 찾을 수 없습니다."));
    }
}
