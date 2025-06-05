package roomescape.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import roomescape.domain.PaymentInfo;
import roomescape.dto.request.PaymentRequest;
import roomescape.dto.response.ReservationResponse;

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
        PaymentInfo paymentInfo = paymentService.createPaymentInfo(request);
        return reservationService.createReservationForMember(memberId, timeId, themeId, date, paymentInfo);
    }
}