package roomescape.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.CanceledReservationRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.dto.LoginMember;
import roomescape.dto.request.payment.PaymentRequest;
import roomescape.dto.request.reservation.AdminReservationRequest;
import roomescape.dto.request.reservation.ReservationCriteriaRequest;
import roomescape.dto.request.reservation.ReservationInformRequest;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.request.reservation.WaitingRequest;
import roomescape.dto.response.reservation.CanceledReservationWebResponse;
import roomescape.dto.response.reservation.MyReservationWebResponse;
import roomescape.dto.response.reservation.ReservationInformResponse;
import roomescape.dto.response.reservation.ReservationResponse;

@Service
public class ReservationService {
    private final PaymentService paymentService;
    private final ReservationCreateService reservationCreateService;
    private final ReservationDeleteService reservationDeleteService;
    private final ReservationRepository reservationRepository;
    private final CanceledReservationRepository canceledReservationRepository;

    public ReservationService(
            PaymentService paymentService,
            ReservationCreateService reservationCreateService,
            ReservationDeleteService reservationDeleteService,
            ReservationRepository reservationRepository,
            CanceledReservationRepository canceledReservationRepository
    ) {
        this.paymentService = paymentService;
        this.reservationCreateService = reservationCreateService;
        this.reservationDeleteService = reservationDeleteService;
        this.reservationRepository = reservationRepository;
        this.canceledReservationRepository = canceledReservationRepository;
    }

    @Transactional
    public ReservationResponse saveReservationWithPaymentByClient(LoginMember loginMember, ReservationRequest reservationRequest) {
        Reservation reservation = reservationCreateService.saveReservationByClient(
                loginMember, reservationRequest
        );
        PaymentRequest paymentRequest = new PaymentRequest(
                reservationRequest.orderId(),
                reservationRequest.amount(),
                reservationRequest.paymentKey()
        );
        paymentService.pay(paymentRequest, reservation);
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse saveWaitingByClient(LoginMember loginMember, WaitingRequest waitingRequest) {
        Reservation reservation = reservationCreateService.saveWaitingByClient(loginMember, waitingRequest);
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse saveReservationByAdmin(AdminReservationRequest adminReservationRequest) {
        Reservation reservation = reservationCreateService.saveReservationByAdmin(adminReservationRequest);
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public void deleteById(long id, LoginMember loginMember) {
        reservationDeleteService.deleteById(id, loginMember);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByStatus(Status status) {
        List<Reservation> reservations = reservationRepository.findAllByStatus(status);
        return convertToReservationResponses(reservations);
    }

    private List<ReservationResponse> convertToReservationResponses(List<Reservation> reservations) {
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findByCriteria(ReservationCriteriaRequest reservationCriteriaRequest) {
        Long themeId = reservationCriteriaRequest.themeId();
        Long memberId = reservationCriteriaRequest.memberId();
        LocalDate dateFrom = reservationCriteriaRequest.dateFrom();
        LocalDate dateTo = reservationCriteriaRequest.dateTo();
        return reservationRepository.findByCriteria(themeId, memberId, dateFrom, dateTo).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MyReservationWebResponse> findMyReservations(Long memberId) {
        return reservationRepository.findMyReservation(memberId).stream()
                .map(myReservationResponse -> MyReservationWebResponse.of(
                        myReservationResponse.reservation(), myReservationResponse.paymentKey(),
                        myReservationResponse.totalAmount(), getWaitingOrder(myReservationResponse.reservation())))
                .toList();
    }

    private long getWaitingOrder(Reservation reservation) {
        return reservationRepository.countByOrder(
                reservation.getId(), reservation.getDate(),
                reservation.getTime().getId(), reservation.getTheme().getId()
        );
    }

    @Transactional
    public ReservationResponse approvePaymentWaiting(long id, ReservationInformRequest reservationRequest) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow();
        reservation.approve();
        PaymentRequest paymentRequest = new PaymentRequest(
                reservationRequest.orderId(),
                reservationRequest.amount(),
                reservationRequest.paymentKey()
        );
        paymentService.pay(paymentRequest, reservation);
        return ReservationResponse.from(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationInformResponse findById(long id) {
        return ReservationInformResponse.from(reservationRepository.findById(id).orElseThrow());
    }

    @Transactional(readOnly = true)
    public List<CanceledReservationWebResponse> findAllCanceledReservation() {
        return canceledReservationRepository.findAllCanceledReservationInform().stream()
                .map(canceledReservationResponse -> CanceledReservationWebResponse.of(
                        canceledReservationResponse.canceledReservation(),
                        canceledReservationResponse.paymentKey(),
                        canceledReservationResponse.totalAmount()))
                .toList();
    }
}
