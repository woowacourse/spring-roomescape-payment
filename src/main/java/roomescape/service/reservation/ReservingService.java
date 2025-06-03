package roomescape.service.reservation;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.service.payment.PaymentService;

@RequiredArgsConstructor
@Service
public class ReservingService {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    @Transactional
    public ReservationResponse reserve(
            LocalDate date,
            long themeId,
            long timeId,
            String paymentKey,
            String orderId,
            int amount,
            long memberId
    ) {
        final CreateReservationRequest createReservationRequest = new CreateReservationRequest(
                memberId,
                date,
                themeId,
                timeId
        );
        final ReservationResponse reservationResponse = reservationService.addReservation(createReservationRequest);
        paymentService.approveAndSave(paymentKey, orderId, amount, reservationResponse.id());
        return reservationResponse;
    }
}
