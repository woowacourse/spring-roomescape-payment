package roomescape.service.booking.reservation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationfilterRequest;
import roomescape.dto.reservation.UserReservationPaymentRequest;
import roomescape.dto.reservation.UserReservationPaymentResponse;
import roomescape.dto.reservation.UserReservationResponse;
import roomescape.dto.waiting.WaitingResponse;
import roomescape.service.booking.reservation.module.PaymentService;
import roomescape.service.booking.reservation.module.ReservationCancelService;
import roomescape.service.booking.reservation.module.ReservationRegisterService;
import roomescape.service.booking.reservation.module.ReservationSearchService;
import roomescape.service.booking.waiting.WaitingService;

@Service
public class ReservationService {

    private final ReservationCancelService reservationCancelService;
    private final ReservationRegisterService reservationRegisterService;
    private final ReservationSearchService reservationSearchService;
    private final PaymentService paymentService;
    private final WaitingService waitingService;

    public ReservationService(ReservationCancelService reservationCancelService,
                              ReservationRegisterService reservationRegisterService,
                              ReservationSearchService reservationSearchService,
                              final PaymentService paymentService,
                              final WaitingService waitingService
    ) {
        this.reservationCancelService = reservationCancelService;
        this.reservationRegisterService = reservationRegisterService;
        this.reservationSearchService = reservationSearchService;
        this.paymentService = paymentService;
        this.waitingService = waitingService;
    }

    @Transactional
    public ReservationResponse registerReservationPayments(UserReservationPaymentRequest userReservationPaymentRequest,
                                                           Long memberId) {
        Long id = reservationRegisterService.registerReservation(userReservationPaymentRequest, memberId);
        return findReservation(id);
    }

    public Long registerReservation(ReservationRequest request) {
        return reservationRegisterService.registerReservation(request);
    }

    public ReservationResponse findReservation(Long reservationId) {
        return reservationSearchService.findReservation(reservationId);
    }

    public List<ReservationResponse> findAllReservations() {
        return reservationSearchService.findAllReservations();
    }

    public List<UserReservationPaymentResponse> findReservationByMemberId(Long memberId) {
        List<UserReservationResponse> reservationResponses = reservationSearchService.findReservationByMemberId(memberId);
        List<UserReservationPaymentResponse> reservationPaymentResponses = new ArrayList<>();

        for (UserReservationResponse reservationResponse : reservationResponses) {
            if (reservationResponse.isReserved()) {
                PaymentResponse paymentResponse = paymentService.findPaymentById(reservationResponse.paymentId());
                reservationPaymentResponses.add(UserReservationPaymentResponse.of(reservationResponse, paymentResponse));
            }
            if (!reservationResponse.isReserved()) {
                WaitingResponse waitingResponse = waitingService.findWaitingByReservationId(reservationResponse.id());
                reservationPaymentResponses.add(UserReservationPaymentResponse.from(waitingResponse));
            }
        }

        return reservationPaymentResponses;
    }

    public List<ReservationResponse> findReservationsByFilter(ReservationfilterRequest filter) {
        return reservationSearchService.findReservationsByFilter(filter);
    }

    @Transactional
    public void deleteReservation(Long reservationId) {
        reservationCancelService.deleteReservation(reservationId);
    }
}
