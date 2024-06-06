package roomescape.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationFactory;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.reservationwaiting.ReservationWaitingRepository;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.request.reservation.AdminReservationRequest;
import roomescape.dto.LoginMember;
import roomescape.dto.response.reservation.MyReservationResponse;
import roomescape.dto.request.reservation.ReservationCriteriaRequest;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.exception.RoomescapeException;

@Service
public class ReservationService {
    private final ReservationFactory reservationFactory;
    private final ReservationRepository reservationRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final PaymentService paymentService;

    public ReservationService(ReservationFactory reservationFactory,
                              ReservationRepository reservationRepository,
                              ReservationWaitingRepository reservationWaitingRepository,
                              PaymentService paymentService) {
        this.reservationRepository = reservationRepository;
        this.reservationFactory = reservationFactory;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationResponse saveReservationByClient(LoginMember loginMember, ReservationRequest reservationRequest) {
        PaymentRequest paymentRequest = reservationRequest.toPaymentRequest();
        Reservation reservation = reservationFactory.createReservation(
                loginMember.id(),
                reservationRequest.date(),
                reservationRequest.timeId(),
                reservationRequest.themeId()
        );
        Reservation savedReservation = reservationRepository.save(reservation);
        paymentService.confirmPayment(paymentRequest, savedReservation);
        return ReservationResponse.from(savedReservation);
    }

    @Transactional
    public ReservationResponse saveReservationByAdmin(AdminReservationRequest adminReservationRequest) {
        Reservation reservation = reservationFactory.createReservation(
                adminReservationRequest.memberId(),
                adminReservationRequest.date(),
                adminReservationRequest.timeId(),
                adminReservationRequest.themeId()
        );
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Transactional
    public void deleteById(long id) {
        Reservation reservation = getReservation(id);
        if (hasReservationWaiting(reservation)) {
            ReservationWaiting reservationWaiting = getReservationWaiting(reservation);
            confirmReservationWaiting(reservationWaiting);
        }
        paymentService.deletePayment(reservation.getId());
        reservationRepository.deleteById(reservation.getId());
    }

    private Reservation getReservation(long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.NOT_FOUND,
                        String.format("존재하지 않는 예약입니다. 요청 예약 id:%d", id)));
    }

    private boolean hasReservationWaiting(Reservation reservation) {
        return reservationWaitingRepository.existsByDateAndTimeIdAndThemeId(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId()
        );
    }

    private ReservationWaiting getReservationWaiting(Reservation reservation) {
        return reservationWaitingRepository.findFirstByDateAndThemeIdAndTimeIdOrderById(
                        reservation.getDate(),
                        reservation.getTheme().getId(),
                        reservation.getTime().getId())
                .orElseThrow(() -> new RoomescapeException(HttpStatus.BAD_REQUEST, "예약 대기가 존재하지 않습니다."));
    }

    private void confirmReservationWaiting(ReservationWaiting reservationWaiting) {
        reservationRepository.save(
                new Reservation(
                        reservationWaiting.getMember(),
                        reservationWaiting.getDate(),
                        reservationWaiting.getTime(),
                        reservationWaiting.getTheme(),
                        Status.PAYMENT_WAITING
                )
        );
        reservationWaitingRepository.delete(reservationWaiting);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        List<Reservation> reservations = reservationRepository.findAll();
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
    public List<MyReservationResponse> findMyReservations(Long memberId) {
        Stream<MyReservationResponse> reservationResponses =
                reservationWaitingRepository.findReservationWaitWithRankByMemberId(memberId).stream()
                .map(MyReservationResponse::from);
        Stream<MyReservationResponse> reservationWaitingResponses =
                reservationRepository.findAllByMemberId(memberId).stream()
                .map(MyReservationResponse::from);
        return Stream.concat(reservationResponses, reservationWaitingResponses)
                .sorted(Comparator.comparing(reservation -> LocalDateTime.of(reservation.date(), reservation.time())))
                .toList();
    }
}
