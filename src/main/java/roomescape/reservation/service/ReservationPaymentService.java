package roomescape.reservation.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import roomescape.auth.dto.LoggedInMember;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.exception.PaymentException;
import roomescape.paymenthistory.service.TossPaymentHistoryService;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSaveResponse;

@Service
public class ReservationPaymentService {

    private final ReservationService reservationService;
    private final TossPaymentHistoryService tossPaymentHistoryService;

    public ReservationPaymentService(ReservationService reservationService,
                                     TossPaymentHistoryService tossPaymentHistoryService) {
        this.reservationService = reservationService;
        this.tossPaymentHistoryService = tossPaymentHistoryService;
    }

    public ReservationResponse saveReservationWithPayment(ReservationCreateRequest reservationCreateRequest,
                                                          LoggedInMember member) {
        ReservationSaveResponse response = reservationService.createReservation(reservationCreateRequest, member.id());

        try {
            tossPaymentHistoryService.approvePayment(
                    new PaymentCreateRequest(reservationCreateRequest.paymentKey(), reservationCreateRequest.orderId(),
                            reservationCreateRequest.amount(), response.reservation()));
            return ReservationResponse.from(response.reservation());
        } catch (PaymentException | DataIntegrityViolationException exception) {
            reservationService.deleteReservation(response.reservation().getId());
            throw exception;
        }
    }
}
