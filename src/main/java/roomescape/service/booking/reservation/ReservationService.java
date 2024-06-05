package roomescape.service.booking.reservation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.waiting.Waiting;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationfilterRequest;
import roomescape.dto.reservation.UserReservationPaymentRequest;
import roomescape.dto.reservation.UserReservationPaymentResponse;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;
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

    private final ReservationRepository reservationRepository; // TODO: 해결
    private final WaitingRepository waitingRepository;
    private final PaymentRepository paymentRepository;

    public ReservationService(ReservationCancelService reservationCancelService,
                              ReservationRegisterService reservationRegisterService,
                              ReservationSearchService reservationSearchService, PaymentService paymentService,
                              final ReservationRepository reservationRepository,
                              final WaitingRepository waitingRepository,
                              final PaymentRepository paymentRepository
    ) {
        this.reservationCancelService = reservationCancelService;
        this.reservationRegisterService = reservationRegisterService;
        this.reservationSearchService = reservationSearchService;
        this.paymentService = paymentService;
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public ReservationResponse registerReservationPayments(UserReservationPaymentRequest userReservationPaymentRequest,
                                                           Long memberId) {
        ReservationRequest reservationRequest = ReservationRequest.of(userReservationPaymentRequest, memberId);
        Long reservationId = reservationRegisterService.registerReservation(reservationRequest); // TODO: 여기 로직 개선

        Reservation reservation = findReservationById(reservationId);
        PaymentRequest paymentRequest = PaymentRequest.from(userReservationPaymentRequest);
        paymentService.pay(paymentRequest, reservation);

        return findReservation(reservationId);
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
        List<Reservation> reservations = reservationRepository.findByMemberId(memberId);
        List<UserReservationPaymentResponse> reservationPaymentResponses = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.isReserved()) {
                Payment payment = paymentRepository.findByReservation(reservation);
                reservationPaymentResponses.add(UserReservationPaymentResponse.of(reservation, payment));
            }
            if (!reservation.isReserved()) {
                Waiting waiting = findWaitingByReservation(reservation);
                reservationPaymentResponses.add(UserReservationPaymentResponse.of(waiting));
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

    public Waiting findWaitingByReservation(Reservation reservation) {
        return waitingRepository.findByReservation(reservation)
                .orElseThrow(() -> new RoomEscapeException(
                        "예약 정보와 일치하는 대기 정보가 존재하지 않습니다.",
                        "reservation_id : " + reservation
                ));
    }
    public Reservation findReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RoomEscapeException(
                        "일치하는 예약 정보가 존재하지 않습니다.",
                        "reservation_id : " + id
                ));
    }
}
