package roomescape.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.dto.LoginMember;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.request.reservation.AdminReservationRequest;
import roomescape.dto.request.reservation.ReservationCriteriaRequest;
import roomescape.dto.request.reservation.ReservationInformRequest;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.request.reservation.WaitingRequest;
import roomescape.dto.response.reservation.MyReservationResponse;
import roomescape.dto.response.reservation.ReservationInformResponse;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.exception.RoomescapeException;

@Service
public class ReservationService {
    private final PaymentService paymentService;
    private final ReservationCreateService reservationCreateService;
    private final ReservationRepository reservationRepository;

    public ReservationService(
            PaymentService paymentService,
            ReservationCreateService reservationCreateService,
            ReservationRepository reservationRepository
    ) {
        this.paymentService = paymentService;
        this.reservationCreateService = reservationCreateService;
        this.reservationRepository = reservationRepository;
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
    public void deleteById(long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.NOT_FOUND,
                        String.format("존재하지 않는 예약입니다. 요청 예약 id:%d", id)));
        reservationRepository.deleteById(reservation.getId());
        updateWaitingToReservation(reservation);
    }

    private void updateWaitingToReservation(Reservation reservation) {
        if (isWaitingUpdatableToReservation(reservation)) {
            reservationRepository.findFirstByDateAndTimeIdAndThemeIdAndStatus(
                    reservation.getDate(), reservation.getTime().getId(), reservation.getTheme().getId(), Status.WAITING
            ).ifPresent(Reservation::changePaymentWaiting);
        }
    }

    private boolean isWaitingUpdatableToReservation(Reservation reservation) {
        return reservation.getStatus() == Status.RESERVATION &&
                reservationRepository.existsByDateAndTimeIdAndThemeIdAndStatus(
                        reservation.getDate(), reservation.getTime().getId(),
                        reservation.getTheme().getId(), Status.WAITING
                );
    }

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

    public List<MyReservationResponse> findMyReservations(Long memberId) {
        List<MyReservationResponse> myReservation = reservationRepository.findMyReservation(memberId);
        return myReservation;
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
        reservation.setStatus(Status.RESERVATION);
        PaymentRequest paymentRequest = new PaymentRequest(
                reservationRequest.orderId(),
                reservationRequest.amount(),
                reservationRequest.paymentKey()
        );
        paymentService.pay(paymentRequest, reservation);
        return ReservationResponse.from(reservation);
    }

    public ReservationInformResponse findById(long id) {
        return ReservationInformResponse.from(reservationRepository.findById(id).orElseThrow());
    }
}
