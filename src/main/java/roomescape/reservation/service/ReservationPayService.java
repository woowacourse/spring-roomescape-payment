package roomescape.reservation.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.service.PaymentCreateService;
import roomescape.payment.service.PaymentFindService;
import roomescape.reservation.domain.PaymentStatus;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.MyReservationWithPaymentResponse;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.UserReservationCreateRequest;

@Service
public class ReservationPayService {
    private static final Comparator<MyReservationWithPaymentResponse> RESERVATION_SORTING_COMPARATOR = Comparator
            .comparing(MyReservationWithPaymentResponse::date).thenComparing(MyReservationWithPaymentResponse::startAt);

    private final ReservationCreateService reservationCreateService;
    private final ReservationFindMineService reservationFindMineService;
    private final ReservationFindService reservationFindService;
    private final ReservationDeleteService reservationDeleteService;
    private final ReservationUpdateService reservationUpdateService;
    private final PaymentCreateService paymentCreateService;
    private final PaymentFindService paymentFindService;

    public ReservationPayService(ReservationCreateService reservationCreateService,
                                 ReservationFindMineService reservationFindMineService,
                                 ReservationFindService reservationFindService,
                                 ReservationDeleteService reservationDeleteService,
                                 PaymentCreateService paymentCreateService,
                                 PaymentFindService paymentFindService,
                                 ReservationUpdateService reservationUpdateService) {
        this.reservationCreateService = reservationCreateService;
        this.reservationFindMineService = reservationFindMineService;
        this.reservationFindService = reservationFindService;
        this.reservationDeleteService = reservationDeleteService;
        this.reservationUpdateService = reservationUpdateService;
        this.paymentCreateService = paymentCreateService;
        this.paymentFindService = paymentFindService;
    }

    public ReservationResponse createReservation(UserReservationCreateRequest request, Long memberId) {
        ReservationResponse reservation = reservationCreateService.createReservation(request, memberId);
        PaymentConfirmRequest paymentConfirmRequest = PaymentConfirmRequest.from(request, reservation.id(), memberId);
        try {
            paymentCreateService.confirmPayment(paymentConfirmRequest);
            reservationUpdateService.updateReservationPaymentStatus(reservation.id(), PaymentStatus.COMPLETED);
        } catch (PaymentException exception) {
            reservationDeleteService.deleteReservation(reservation.id());
            throw exception;
        }
        return reservation;
    }

    public List<MyReservationWithPaymentResponse> findMyReservationsWithPayment(Long memberId) {
        List<MyReservationResponse> reservations = reservationFindMineService.findReservations(memberId);
        List<MyReservationResponse> waitings = reservationFindMineService.findWaitings(memberId);
        List<PaymentResponse> payment = paymentFindService.findPayment(memberId);

        List<MyReservationWithPaymentResponse> reservationResponses = reservations.stream()
                .map(reservationResponse -> getMyReservationWithPaymentResponse(reservationResponse, payment))
                .toList();
        List<MyReservationWithPaymentResponse> waitingResponses = waitings.stream()
                .map(MyReservationWithPaymentResponse::from)
                .toList();

        return makeMyReservations(reservationResponses, waitingResponses);
    }

    public MyReservationResponse updateReservationPayment(ReservationPaymentRequest request, Long reservationId, Long memberId) {
        ReservationResponse reservation = reservationFindService.findReservation(reservationId);
        PaymentConfirmRequest paymentConfirmRequest = PaymentConfirmRequest.from(request, reservation.id(), memberId);

        paymentCreateService.confirmPayment(paymentConfirmRequest);
        return reservationUpdateService.updateReservationPaymentStatus(reservation.id(), PaymentStatus.COMPLETED);
    }

    private MyReservationWithPaymentResponse getMyReservationWithPaymentResponse(MyReservationResponse reservation, List<PaymentResponse> payments) {
        return payments.stream()
                .filter(paymentResponse -> paymentResponse.reservationId().equals(reservation.id()))
                .findFirst()
                .map(paymentResponse -> MyReservationWithPaymentResponse.from(reservation, paymentResponse))
                .orElseGet(() -> MyReservationWithPaymentResponse.from(reservation));
    }

    private List<MyReservationWithPaymentResponse> makeMyReservations(
            List<MyReservationWithPaymentResponse> reservations,
            List<MyReservationWithPaymentResponse> waitings) {
        List<MyReservationWithPaymentResponse> response = new ArrayList<>();
        response.addAll(reservations);
        response.addAll(waitings);
        response.sort(RESERVATION_SORTING_COMPARATOR);
        return response;
    }
}
