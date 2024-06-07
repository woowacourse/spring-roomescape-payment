package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.BadArgumentRequestException;
import roomescape.reservation.domain.PaymentStatus;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.repository.ReservationRepository;

@Service
public class ReservationUpdateService {
    private final ReservationRepository reservationRepository;

    public ReservationUpdateService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public MyReservationResponse updateReservationPaymentStatus(Long id, PaymentStatus paymentStatus) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new BadArgumentRequestException("해당 예약을 찾을 수 없습니다."));
        reservation.updatePaymentStatus(paymentStatus);
        return MyReservationResponse.from(reservation);
    }
}
