package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.Waitings;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSaveRequest;
import roomescape.reservation.dto.ReservationSearchConditionRequest;
import roomescape.reservation.dto.ReservationWaitingResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    public ReservationResponse saveReservationPending(
            ReservationSaveRequest reservationSaveRequest,
            LoginMember loginMember
    ) {
        Reservation pendingReservation = createValidatedReservationOfStatus(reservationSaveRequest, loginMember.id(),
                ReservationStatus.PENDING);
        validateDuplicatedReservationSuccess(pendingReservation);
        Reservation savedReservation = reservationRepository.save(pendingReservation);

        return ReservationResponse.toResponse(savedReservation);
    }

    public void rollBackPendingReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("결제 대기중인 예약이 아닙니다.");
        }
        reservationRepository.deleteById(id);
    }

    public ReservationResponse saveReservationSuccessByAdmin(ReservationSaveRequest reservationSaveRequest) {
        Reservation reservation = createValidatedReservationOfStatus(reservationSaveRequest, reservationSaveRequest.getMemberId(), ReservationStatus.SUCCESS);
        validateDuplicatedReservationSuccess(reservation);
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.toResponse(savedReservation);
    }

    public ReservationResponse saveReservationWaiting(
            ReservationSaveRequest reservationSaveRequest,
            LoginMember loginMember
    ) {
        Reservation reservation = createValidatedReservationOfStatus(reservationSaveRequest, loginMember.id(), ReservationStatus.WAIT);
        validateDuplicatedReservationWaiting(reservation, loginMember);
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.toResponse(savedReservation);
    }

    public ReservationResponse confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("결제 대기중인 예약이 아닙니다.");
        }
        reservation.updateStatus(ReservationStatus.SUCCESS);
        return ReservationResponse.toResponse(reservation);
    }

    private Reservation createValidatedReservationOfStatus(
            ReservationSaveRequest reservationSaveRequest,
            Long memberId,
            ReservationStatus reservationStatus
    ) {
        ReservationTime reservationTime = reservationTimeRepository.findById(reservationSaveRequest.getTimeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 시간입니다."));

        Theme theme = themeRepository.findById(reservationSaveRequest.getThemeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테마입니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return reservationSaveRequest.toReservation(member, theme, reservationTime, reservationStatus);
    }

    private void validateDuplicatedReservationSuccess(Reservation reservation) {
        boolean existsReservation = reservationRepository.existsByDateAndTimeStartAtAndStatus(
                reservation.getDate(),
                reservation.getStartAt(),
                ReservationStatus.SUCCESS
        );

        if (existsReservation) {
            throw new IllegalArgumentException("중복된 예약이 있습니다.");
        }
    }

    private void validateDuplicatedReservationWaiting(Reservation reservation, LoginMember loginMember) {
        List<ReservationStatus> reservationStatuses = reservationRepository.findStatusesByMemberIdAndDateAndTimeStartAt(
                loginMember.id(),
                reservation.getDate(),
                reservation.getStartAt()
        );

        if (reservationStatuses.contains(ReservationStatus.SUCCESS) || reservationStatuses.contains(ReservationStatus.WAIT)) {
            throw new IllegalArgumentException("예약이 완료되었거나, 대기 상태로 등록된 예약입니다.");
        }
    }

    @Transactional(readOnly = true)
    public ReservationResponse findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        return ReservationResponse.toResponse(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByStatus(ReservationStatus reservationStatus) {
        List<Reservation> waitingReservations = reservationRepository.findAllByStatusFromDate(reservationStatus, LocalDate.now());

        return waitingReservations.stream()
                .map(ReservationResponse::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberReservationResponse> findMemberReservations(LoginMember loginMember) {
        List<Reservation> waitingReservations = reservationRepository.findAllByStatusFromDate(ReservationStatus.WAIT, LocalDate.now());

        Waitings waitings = new Waitings(waitingReservations);

        return reservationRepository.findAllExceptCancelByMemberIdFromDateOrderByDateAscTimeStartAtAscCreatedAtAsc(loginMember.id(), LocalDate.now()).stream()
                .map(reservationWithPayment -> MemberReservationResponse.toResponse(
                        reservationWithPayment,
                        waitings.findMemberRank(reservationWithPayment.reservation(), loginMember.id())
                )).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationWaitingResponse> findWaitingReservations() {
        List<Reservation> waitingReservations = reservationRepository.findAllByStatusFromDate(ReservationStatus.WAIT, LocalDate.now());

        return waitingReservations.stream()
                .map(ReservationWaitingResponse::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllBySearchCondition(ReservationSearchConditionRequest request) {
        return reservationRepository.findAllByThemeIdAndMemberIdAndDateBetweenOrderByDateAscTimeStartAtAscCreatedAtAsc(
                        request.themeId(),
                        request.memberId(),
                        request.dateFrom(),
                        request.dateTo()
                ).stream()
                .map(ReservationResponse::toResponse)
                .toList();
    }

    public void deleteWaitingById(Long id) {
        reservationRepository.deleteById(id);
    }

    public void cancelReservationById(Long id) {
        Reservation canceledReservation = getCanceledReservation(id);
        updateFirstWaitingReservation(canceledReservation);
    }

    private Reservation getCanceledReservation(Long id) {
        Reservation successReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        if (successReservation.getDate().equals(LocalDate.now())) {
            throw new IllegalArgumentException("당일 예약은 취소할 수 없습니다.");
        }
        successReservation.updateStatus(ReservationStatus.CANCEL);

        return successReservation;
    }

    private void updateFirstWaitingReservation(Reservation canceledReservation) {
        reservationRepository.findFirstWaitingReservationBy(
                canceledReservation.getDate(),
                canceledReservation.getTime().getId(),
                canceledReservation.getTheme().getId()
        ).ifPresent(reservation -> reservation.updateStatus(ReservationStatus.SUCCESS));
    }
}
