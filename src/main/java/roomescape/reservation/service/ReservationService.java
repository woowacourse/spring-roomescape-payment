package roomescape.reservation.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Status;
import roomescape.reservation.dto.request.PaymentConfirmRequest;
import roomescape.reservation.dto.request.ReservationDetailRequest;
import roomescape.reservation.dto.request.ReservationSaveRequest;
import roomescape.reservation.dto.request.ReservationSearchCondRequest;
import roomescape.reservation.dto.response.MemberReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationFactoryService reservationFactoryService;
    private final ReservationSchedulerService reservationSchedulerService;
    private final PaymentService paymentService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationFactoryService reservationFactoryService,
            ReservationSchedulerService reservationSchedulerService,
            PaymentService paymentService
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationFactoryService = reservationFactoryService;
        this.reservationSchedulerService = reservationSchedulerService;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationResponse save(ReservationDetailRequest detailRequest, long memberId) {
        ReservationSaveRequest saveRequest = ReservationSaveRequest.of(detailRequest, memberId);
        Reservation savedReservation = createReservation(saveRequest);
        paymentService.confirmPayment(PaymentConfirmRequest.from(detailRequest));

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

    public ReservationResponse findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        return ReservationResponse.toResponse(reservation);
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAllByStatus(Status.SUCCESS)
                .stream()
                .map(ReservationResponse::toResponse)
                .toList();
    }

    public List<ReservationResponse> findAllBySearchCond(ReservationSearchCondRequest request) {
        return findAllReservationsWithCond(request)
                .stream()
                .map(ReservationResponse::toResponse)
                .toList();
    }

    public List<MemberReservationResponse> findReservationsAndWaitingsByMember(LoginMember loginMember) {
        return reservationRepository.findReservationWithRanksByMemberId(loginMember.id())
                .stream()
                .map(MemberReservationResponse::toResponse)
                .toList();
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
