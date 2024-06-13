package roomescape.service.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.infrastructure.tosspayments.TossPaymentsClient;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.service.reservation.module.ReservationMapper;
import roomescape.service.reservation.module.ReservationValidator;

@Service
@Transactional
public class ReservationRegisterService {

    private final ReservationMapper reservationMapper;
    private final ReservationValidator reservationValidator;
    private final TossPaymentsClient tossPaymentsClient;

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;


    public ReservationRegisterService(ReservationMapper reservationMapper,
                                      ReservationValidator reservationValidator,
                                      TossPaymentsClient tossPaymentsClient,
                                      ReservationRepository reservationRepository,
                                      PaymentRepository paymentRepository
    ) {
        this.reservationMapper = reservationMapper;
        this.reservationValidator = reservationValidator;
        this.tossPaymentsClient = tossPaymentsClient;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    public ReservationResponse registerReservation(ReservationRequest reservationRequest,
                                                   PaymentRequest paymentRequest
    ) {
        Reservation reservation = createAndSaveReservation(reservationRequest, Status.RESERVED);
        processPayment(paymentRequest, reservation.getId());
        return ReservationResponse.from(reservation);
    }

    public ReservationResponse registerWaitingReservation(ReservationRequest request) {
        Reservation reservation = createAndSaveReservation(request, Status.WAITING);
        return ReservationResponse.from(reservation);
    }

    public ReservationResponse registerReservationByAdmin(ReservationRequest request) {
        Reservation reservation = createAndSaveReservation(request, Status.RESERVED);
        return ReservationResponse.from(reservation);
    }

    public ReservationResponse requestPaymentByPaymentPending(Long reservationId, PaymentRequest paymentRequest) {
        Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);
        reservationValidator.validatePaymentPendingStatus(reservation);
        reservationValidator.validateReservationAvailability(reservation);
        processPayment(paymentRequest, reservation.getId());
        reservation.changeStatusToReserve();
        return ReservationResponse.from(reservation);
    }

    private void processPayment(PaymentRequest paymentRequest, Long reservationId) {
        reservationValidator.validatePaymentAvailability(reservationId);
        PaymentResponse paymentResponse = tossPaymentsClient.requestPayment(paymentRequest);
        Payment payment = paymentResponse.toEntity(reservationId);
        paymentRepository.save(payment);
    }

    private Reservation createAndSaveReservation(ReservationRequest request, Status status) {
        Reservation reservation = reservationMapper.mapperOf(request, status);

        if (status == Status.RESERVED) {
            reservationValidator.validateReservationAvailability(reservation);
        }
        if (status == Status.WAITING) {
            reservationValidator.validateWaitingAddable(reservation);
        }

        return reservationRepository.save(reservation);
    }
}
