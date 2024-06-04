package roomescape.service.booking.reservation;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.reservation.ReservationFilter;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.UserReservationRequest;
import roomescape.dto.reservation.UserReservationResponse;
import roomescape.service.booking.reservation.module.PaymentService;
import roomescape.service.booking.reservation.module.ReservationCancelService;
import roomescape.service.booking.reservation.module.ReservationRegisterService;
import roomescape.service.booking.reservation.module.ReservationSearchService;

@Service
public class ReservationService {

    private final ReservationCancelService reservationCancelService;
    private final ReservationRegisterService reservationRegisterService;
    private final ReservationSearchService reservationSearchService;
    private final PaymentService paymentService;

    public ReservationService(ReservationCancelService reservationCancelService,
                              ReservationRegisterService reservationRegisterService,
                              ReservationSearchService reservationSearchService, PaymentService paymentService
    ) {
        this.reservationCancelService = reservationCancelService;
        this.reservationRegisterService = reservationRegisterService;
        this.reservationSearchService = reservationSearchService;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationResponse registerReservationWithPayment(UserReservationRequest userReservationRequest,
                                                              Long memberId
    ) {
        ReservationRequest reservationRequest = userReservationRequest.toReservationRequest(memberId);
        PaymentRequest paymentRequest = userReservationRequest.toPaymentRequest();

        ReservationResponse reservationResponse = reservationRegisterService.registerReservation(reservationRequest);
        paymentService.pay(paymentRequest);
        return reservationResponse;
    }

    public ReservationResponse registerReservationWithoutPayment(ReservationRequest reservationRequest) {
        return reservationRegisterService.registerReservation(reservationRequest);
    }

    public ReservationResponse findReservation(Long reservationId) {
        return reservationSearchService.findReservation(reservationId);
    }

    public List<ReservationResponse> findAllReservations() {
        return reservationSearchService.findAllReservations();
    }

    public List<UserReservationResponse> findReservationByMemberId(Long memberId) {
        return reservationSearchService.findReservationByMemberId(memberId);
    }

    public List<ReservationResponse> findReservationsByFilter(ReservationFilter filter) {
        return reservationSearchService.findReservationsByFilter(filter);
    }

    public void deleteReservation(Long reservationId) {
        reservationCancelService.deleteReservation(reservationId);
    }
}
