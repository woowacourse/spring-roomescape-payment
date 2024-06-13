package roomescape.reservation.service.component;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.Payment;
import roomescape.payment.service.PaymentService;
import roomescape.payment.service.dto.request.PaymentConfirmRequest;
import roomescape.reservation.controller.dto.request.ReservationSaveRequest;
import roomescape.reservation.controller.dto.request.ReservationSearchCondRequest;
import roomescape.reservation.controller.dto.response.ReservationResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.service.dto.request.ReservationPaymentSaveRequest;
import roomescape.reservation.service.module.ReservationCreateService;
import roomescape.reservation.service.module.ReservationPlanService;
import roomescape.reservation.service.module.ReservationQueryService;

@Service
public class ReservationComponentService {

    private final ReservationQueryService reservationQueryService;
    private final ReservationCreateService reservationCreateService;
    private final ReservationPlanService reservationPlanService;
    private final PaymentService paymentService;

    public ReservationComponentService(
            ReservationQueryService reservationQueryService, ReservationCreateService reservationCreateService,
            ReservationPlanService reservationPlanService,
            PaymentService paymentService
    ) {
        this.reservationQueryService = reservationQueryService;
        this.reservationCreateService = reservationCreateService;
        this.reservationPlanService = reservationPlanService;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationResponse saveWithPayment(ReservationPaymentSaveRequest request) {
        ReservationSaveRequest reservationSaveRequest = ReservationSaveRequest.from(request);
        Reservation savedReservation = createReservation(reservationSaveRequest);
        Payment payment = paymentService.confirm(PaymentConfirmRequest.from(request), savedReservation);
        paymentService.save(payment);

        return ReservationResponse.toResponse(savedReservation);
    }

    @Transactional
    public ReservationResponse saveWithoutPayment(ReservationSaveRequest saveRequest) {
        Reservation savedReservation = createReservation(saveRequest);

        return ReservationResponse.toResponse(savedReservation);
    }

    private Reservation createReservation(ReservationSaveRequest saveRequest) {
        Reservation reservation = reservationCreateService.createReservation(saveRequest);
        reservationPlanService.validateSaveReservation(reservation);

        return reservationQueryService.save(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        return reservationQueryService.findAll()
                .stream()
                .map(ReservationResponse::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllBySearchCond(ReservationSearchCondRequest request) {
        return reservationQueryService.findAllWithSearchCond(request)
                .stream()
                .map(ReservationResponse::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        paymentService.deleteByReservationId(id);
        reservationQueryService.delete(id);
    }
}
