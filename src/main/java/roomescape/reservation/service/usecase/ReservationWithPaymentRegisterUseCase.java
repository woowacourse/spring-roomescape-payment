package roomescape.reservation.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.toss.domain.TossPayment;
import roomescape.payment.toss.dto.TossPaymentRequest;
import roomescape.payment.toss.service.TossPaymentService;
import roomescape.reservation.controller.request.ReservePaymentRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.service.command.ReserveCommand;
import roomescape.reservation.service.manager.ReservationManager;

@Service
@RequiredArgsConstructor
public class ReservationWithPaymentRegisterUseCase {

    private final ReservationManager reservationManager;
    private final TossPaymentService tossPaymentService;

    @Transactional
    public TossPayment execute(final ReservePaymentRequest request, final Long memberId) {
        Reservation reservation = reservationManager.reserved(ReserveCommand.byPayment(request, memberId));
        return tossPaymentService.savePayment(reservation, TossPaymentRequest.from(request));
    }
}
