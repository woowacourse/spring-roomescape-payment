package roomescape.reservation.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.controller.dto.request.ReservationSaveRequest;
import roomescape.reservation.controller.dto.request.ReservationSearchCondRequest;
import roomescape.reservation.controller.dto.response.MemberReservationResponse;
import roomescape.reservation.controller.dto.response.ReservationResponse;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationWithRank;
import roomescape.reservation.domain.Status;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.dto.request.PaymentConfirmRequest;
import roomescape.reservation.service.dto.request.ReservationPaymentRequest;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationFactoryService reservationFactoryService;
    private final ReservationSchedulerService reservationSchedulerService;
    private final PaymentService paymentService;
    private final ReservationPaymentService reservationPaymentService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationFactoryService reservationFactoryService,
            ReservationSchedulerService reservationSchedulerService,
            PaymentService paymentService,
            ReservationPaymentService reservationPaymentService
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationFactoryService = reservationFactoryService;
        this.reservationSchedulerService = reservationSchedulerService;
        this.paymentService = paymentService;
        this.reservationPaymentService = reservationPaymentService;
    }

    @Transactional
    public ReservationResponse save(ReservationPaymentRequest request) {
        ReservationSaveRequest reservationSaveRequest = ReservationSaveRequest.from(request);
        Reservation savedReservation = createReservation(reservationSaveRequest);

        Payment savedPayment = paymentService.confirmAndSave(PaymentConfirmRequest.from(request), savedReservation);
        reservationPaymentService.save(savedReservation, savedPayment);
        return ReservationResponse.toResponse(savedReservation);
    }

    @Transactional
    public ReservationResponse saveByAdmin(ReservationSaveRequest saveRequest) {
        Reservation savedReservation = createReservation(saveRequest);
        return ReservationResponse.toResponse(savedReservation);
    }

    private Reservation createReservation(ReservationSaveRequest saveRequest) {
        Reservation reservation = reservationFactoryService.createSuccess(saveRequest);
        reservationSchedulerService.validateSaveReservation(reservation);
        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationResponse findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        return ReservationResponse.toResponse(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        return reservationRepository.findAllByStatus(Status.SUCCESS)
                .stream()
                .map(ReservationResponse::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllBySearchCond(ReservationSearchCondRequest request) {
        return findAllReservationsWithCond(request)
                .stream()
                .map(ReservationResponse::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberReservationResponse> findReservationsAndWaitingsByMember(LoginMember loginMember) {
        List<ReservationWithRank> reservationWithRanks =
                reservationRepository.findReservationWithRanksByMemberId(loginMember.id());
        return reservationWithRanks.stream()
                .map(reservationWithRank -> MemberReservationResponse.toResponse(
                        reservationWithRank,
                        reservationPaymentService.findPaymentByReservationId(
                                reservationWithRank.getReservation().getId()
                        )
                )).toList();
    }

    @Transactional
    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }

    private List<Reservation> findAllReservationsWithCond(ReservationSearchCondRequest request) {
        return reservationRepository.findAllByThemeIdAndMemberIdAndDateBetweenAndStatus(
                request.themeId(),
                request.memberId(),
                request.dateFrom(),
                request.dateTo(),
                Status.SUCCESS
        );
    }
}
