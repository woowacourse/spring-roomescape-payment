package roomescape.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import roomescape.dto.request.PaymentRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.infrastructure.payment.PaymentDto;

@Service
public class ReservationFacade {
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationFacade(final ReservationService reservationService, final PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    public ReservationResponse processReservationForMember(Long memberId,
                                                           Long timeId,
                                                           Long themeId,
                                                           LocalDate date,
                                                           PaymentRequest request) {
        PaymentDto paymentDto = paymentService.approve(request);
        return reservationService.reserveWithPayment(memberId, timeId, themeId, date, paymentDto);
    }
}