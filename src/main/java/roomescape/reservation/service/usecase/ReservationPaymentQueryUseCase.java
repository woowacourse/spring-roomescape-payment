package roomescape.reservation.service.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.reservation.domain.ReservationPayment;
import roomescape.reservation.repository.ReservationPaymentRepository;

@RequiredArgsConstructor
@Service
public class ReservationPaymentQueryUseCase {

    private final ReservationPaymentRepository reservationPaymentRepository;

    public List<ReservationPayment> getByMemberId(final Long memberId) {
        return reservationPaymentRepository.findByReservationInfoMemberId(memberId);
    }
}
