package roomescape.reservation.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.paymenthistory.service.TossPaymentHistoryService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.AdminReservationCreateRequest;
import roomescape.reservation.dto.MyReservationWaitingResponse;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSaveResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.TimeRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.repository.WaitingRepository;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final TimeRepository timeRepository;
    private final ThemeRepository themeRepository;
    private final WaitingRepository waitingRepository;
    private final TossPaymentHistoryService tossPaymentHistoryService;

    public ReservationService(ReservationRepository reservationRepository, MemberRepository memberRepository,
                              TimeRepository timeRepository, ThemeRepository themeRepository,
                              WaitingRepository waitingRepository,
                              TossPaymentHistoryService tossPaymentHistoryService) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
        this.waitingRepository = waitingRepository;
        this.tossPaymentHistoryService = tossPaymentHistoryService;
    }

    public List<ReservationResponse> findReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> findReservations(ReservationSearchRequest request) {
        return reservationRepository.findAllByCondition(request.memberId(), request.themeId(), request.startDate(),
                        request.endDate())
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<MyReservationWaitingResponse> findMyReservations(Long memberId) {
        return reservationRepository.findByMember_id(memberId)
                .stream()
                .map(MyReservationWaitingResponse::from)
                .toList();
    }

    public ReservationResponse createAdminReservation(AdminReservationCreateRequest request) {
        Member member = findMemberByMemberId(request.memberId());
        ReservationTime time = findTimeByTimeId(request.timeId());
        Theme theme = findThemeByThemeId(request.themeId());
        Reservation reservation = request.createReservation(member, time, theme);

        validateExists(reservation);
        return createReservation(reservation);
    }

    @Transactional
    public ReservationSaveResponse createReservation(ReservationCreateRequest request, Long memberId) {
        Member member = findMemberByMemberId(memberId);
        ReservationTime time = findTimeByTimeId(request.timeId());
        Theme theme = findThemeByThemeId(request.themeId());
        Reservation reservation = request.makeReservation(member, time, theme);
        validateExists(reservation);

        reservationRepository.save(reservation);
        return new ReservationSaveResponse(reservation);
    }

    private void validateExists(Reservation reservation) {
        if (reservationRepository.existsByDateAndTime_idAndTheme_id(
                reservation.getDate(), reservation.getTimeId(), reservation.getThemeId())) {
            throw new IllegalArgumentException("해당 날짜와 시간에 이미 예약된 테마입니다.");
        }
    }

    private ReservationResponse createReservation(Reservation reservation) {
        Reservation createdReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(createdReservation);
    }

    private Member findMemberByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
    }

    private ReservationTime findTimeByTimeId(Long timeId) {
        return timeRepository.findById(timeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 시간이 존재하지 않습니다."));
    }

    private Theme findThemeByThemeId(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 테마가 존재하지 않습니다."));
    }

    @Transactional
    public void deleteWaiting(Long id) {
        waitingRepository.findFirstByReservation_idOrderByCreatedAtAsc(id)
                .ifPresentOrElse(this::promoteWaiting, () -> cancelReservationAndPayment(id));

    }

    public void deleteReservation(Long reservationId) {
        reservationRepository.deleteById(reservationId);
    }

    private void promoteWaiting(Waiting waiting) {
        tossPaymentHistoryService.cancelPayment(waiting.getReservation().getId());

        Reservation promotedReservation = waiting.promoteToReservation();
        reservationRepository.save(promotedReservation);
        waitingRepository.deleteById(waiting.getId());
    }

    private void cancelReservationAndPayment(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약은 존재하지 않습니다."));

        if (reservation.isNotPaidReservation()) {
            reservationRepository.deleteById(id);
            return;
        }

        tossPaymentHistoryService.cancelPayment(id);
        reservationRepository.deleteById(id);
    }
}
