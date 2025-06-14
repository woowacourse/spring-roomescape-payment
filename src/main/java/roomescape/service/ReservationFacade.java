package roomescape.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import roomescape.dto.request.PaymentRequest;
import roomescape.dto.response.PaymentResponse;
import roomescape.dto.response.ReservationForMemberResponse;

@Service
public class ReservationFacade {
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationFacade(final ReservationService reservationService, final PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    public ReservationForMemberResponse processReservationForMember(Long memberId,
                                                                    Long timeId,
                                                                    Long themeId,
                                                                    LocalDate date,
                                                                    PaymentRequest request) {
        PaymentResponse paymentResponse = paymentService.approve(request);
        return reservationService.reserveWithPayment(memberId, timeId, themeId, date, paymentResponse);
    }
}