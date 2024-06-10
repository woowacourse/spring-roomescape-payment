package roomescape.service.booking.reservation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Reservations;
import roomescape.domain.reservation.Status;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationfilterRequest;
import roomescape.dto.reservation.UserReservationPaymentRequest;
import roomescape.dto.reservation.UserReservationPaymentResponse;
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

    public ReservationResponse registerReservationPayments(UserReservationPaymentRequest userReservationPaymentRequest,
                                                           Long memberId) {
        ReservationRequest reservationRequest = ReservationRequest.of(userReservationPaymentRequest, memberId);
        Reservation reservation = reservationRegisterService.registerReservation(reservationRequest);
        PaymentResponse paymentResponse = PaymentResponse.empty();

        try {
            PaymentRequest paymentRequest = PaymentRequest.from(userReservationPaymentRequest);
            paymentResponse = paymentService.payByToss(paymentRequest);
        } catch (Exception e) {
            reservationCancelService.deleteReservation(reservation.getId());
        }

        paymentService.save(paymentResponse.toEntity(reservation));
        return ReservationResponse.from(reservation);
    }

    public ReservationResponse registerReservation(ReservationRequest request) {
        Reservation reservation = reservationRegisterService.registerReservation(request);
        return ReservationResponse.from(reservation);
    }

    public ReservationResponse findReservation(Long reservationId) {
        return reservationSearchService.findReservation(reservationId);
    }

    public List<ReservationResponse> findAllReservations() {
        return reservationSearchService.findAllReservations();
    }

    @Transactional
    public List<UserReservationPaymentResponse> findReservationByMemberId(Long memberId) {
        List<Reservation> reservations = reservationSearchService.findReservationByMemberId(memberId);
        List<UserReservationPaymentResponse> reservationPaymentResponses = new ArrayList<>();

        reservationPaymentResponses.addAll(getReservedReservations(reservations));
        reservationPaymentResponses.addAll(getPendingReservations(reservations));
        reservationPaymentResponses.addAll(getWaitingReservations(reservations));

        return reservationPaymentResponses;
    }

    public List<ReservationResponse> findReservationsByFilter(ReservationfilterRequest filter) {
        return reservationSearchService.findReservationsByFilter(filter);
    }

    private List<UserReservationPaymentResponse> getReservedReservations(final List<Reservation> reservations) {
        List<Reservation> reservedReservations = new Reservations(reservations).filterStatus(Status.RESERVED);
        List<Long> reservedReservationIds = new Reservations(reservedReservations).getIds();
        List<Payment> payments = paymentService.findPaymentByReservationIds(reservedReservationIds);

        return UserReservationPaymentResponse.ofReservations(reservedReservations, payments);
    }

    private List<UserReservationPaymentResponse> getPendingReservations(final List<Reservation> reservations) {
        List<Reservation> pendingReservations = new Reservations(reservations).filterStatus(Status.PENDING);

        return UserReservationPaymentResponse.fromPendings(pendingReservations);
    }

    private List<UserReservationPaymentResponse> getWaitingReservations(final List<Reservation> reservations) {
        List<Reservation> waitingReservations = new Reservations(reservations).filterStatus(Status.WAITING);
        List<Long> waitingReservationIds = new Reservations(waitingReservations).getIds();
        List<WaitingResponse> waitings = waitingService.findPaymentByReservationIds(waitingReservationIds);

        return UserReservationPaymentResponse.fromWaitings(waitings);
    }

    @Transactional
    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationSearchService.findReservationById(reservationId);
        reservationCancelService.deleteReservation(reservation);
    }
}
