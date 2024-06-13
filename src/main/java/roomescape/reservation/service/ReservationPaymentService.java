package roomescape.reservation.service;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import roomescape.auth.dto.LoggedInMember;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.dto.PaymentResponse;
import roomescape.paymenthistory.exception.PaymentException;
import roomescape.paymenthistory.service.TossPaymentHistoryService;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.MyReservationWaitingResponse;
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

    public List<MyReservationWaitingResponse> findMyReservationsWithPayment(long memberId) {
        List<MyReservationResponse> reservations = reservationService.findMyReservations(memberId);

        return reservations.stream()
                .map(this::getReservationWaitingResponse)
                .toList();
    }

    private MyReservationWaitingResponse getReservationWaitingResponse(MyReservationResponse reservationResponse) {
        if (reservationResponse.isPaymentPending()) {
            return MyReservationWaitingResponse.from(reservationResponse);
        }
        PaymentResponse paymentResponse = tossPaymentHistoryService.findPaymentHistory(
                reservationResponse.reservationId());
        return MyReservationWaitingResponse.from(reservationResponse, paymentResponse);
    }
}
