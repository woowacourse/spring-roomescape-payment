package roomescape.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.AdminReservationCreateRequest;
import roomescape.reservation.dto.MyReservationWaitingResponse;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationResponse;
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
    private final PaymentService paymentService;

    public ReservationService(ReservationRepository reservationRepository, MemberRepository memberRepository,
                              TimeRepository timeRepository, ThemeRepository themeRepository,
                              WaitingRepository waitingRepository, PaymentService paymentService) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
        this.waitingRepository = waitingRepository;
        this.paymentService = paymentService;
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
                .map(this::toMyReservationWaitingResponse)
                .toList();
    }

    private MyReservationWaitingResponse toMyReservationWaitingResponse(Reservation reservation) {
        return paymentService.findPaymentByReservation(reservation)
                .map(payment -> MyReservationWaitingResponse.from(reservation, payment))
                .orElseGet(() -> MyReservationWaitingResponse.from(reservation));
    }

    public ReservationResponse createAdminReservation(AdminReservationCreateRequest request) {
        Member member = findMemberByMemberId(request.memberId());
        ReservationTime time = findTimeByTimeId(request.timeId());
        Theme theme = findThemeByThemeId(request.themeId());
        Reservation reservation = request.createReservation(member, time, theme);
        validateCreate(reservation);

        return createReservation(reservation);
    }

    @Transactional
    public ReservationResponse createReservation(ReservationCreateRequest request, Long memberId) {
        Member member = findMemberByMemberId(memberId);
        ReservationTime time = findTimeByTimeId(request.timeId());
        Theme theme = findThemeByThemeId(request.themeId());
        Reservation reservation = request.createReservation(member, time, theme);
        validateCreate(reservation);

        ReservationResponse reservationResponse = createReservation(reservation);
        paymentService.approvePayment(request.createPaymentRequest(reservation));

        return reservationResponse;
    }

    private void validateCreate(Reservation reservation) {
        validateIsAvailable(reservation);
        validateExists(reservation);
    }

    private void validateIsAvailable(Reservation reservation) {
        if (reservation.isBefore(LocalDateTime.now())) {
            throw new RoomEscapeException("예약은 현재 시간 이후여야 합니다.", ExceptionTitle.ILLEGAL_USER_REQUEST);
        }
    }

    private void validateExists(Reservation reservation) {
        if (reservationRepository.existsByDateAndTime_idAndTheme_id(
                reservation.getDate(), reservation.getTimeId(), reservation.getThemeId())) {
            throw new RoomEscapeException("해당 날짜와 시간에 이미 예약된 테마입니다.", ExceptionTitle.ILLEGAL_USER_REQUEST);
        }
    }

    private ReservationResponse createReservation(Reservation reservation) {
        Reservation createdReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(createdReservation);
    }

    private Member findMemberByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new RoomEscapeException("해당 멤버가 존재하지 않습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
    }

    private ReservationTime findTimeByTimeId(Long timeId) {
        return timeRepository.findById(timeId)
                .orElseThrow(() ->
                        new RoomEscapeException("해당 예약 시간이 존재하지 않습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
    }

    private Theme findThemeByThemeId(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() ->
                        new RoomEscapeException("해당 테마가 존재하지 않습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
    }

    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new RoomEscapeException("해당 예약은 존재하지 않습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
        validateDelete(reservation);

        waitingRepository.findFirstByReservation_idOrderByCreatedAtAsc(id)
                .ifPresentOrElse(this::promoteWaiting, () -> cancelReservation(reservation));

    }

    private void validateDelete(Reservation reservation) {
        validatePastReservation(reservation);
        validateEqualsDateReservation(reservation);
    }

    private void validatePastReservation(Reservation reservation) {
        if (reservation.isBefore(LocalDateTime.now())) {
            throw new RoomEscapeException("과거 예약에 대한 취소는 불가능합니다.", ExceptionTitle.ILLEGAL_USER_REQUEST);
        }
    }

    private void validateEqualsDateReservation(Reservation reservation) {
        if (reservation.isEqualsDate(LocalDate.now())) {
            throw new RoomEscapeException("당일 예약 취소는 불가능합니다.", ExceptionTitle.ILLEGAL_USER_REQUEST);
        }
    }

    private void promoteWaiting(Waiting waiting) {
        paymentService.cancelPayment(waiting.getReservation().getId());

        Reservation promotedReservation = waiting.promoteToReservation();
        reservationRepository.save(promotedReservation);
        waitingRepository.deleteById(waiting.getId());
    }

    private void cancelReservation(Reservation reservation) {
        if (reservation.isNotPaidReservation()) {
            reservationRepository.deleteById(reservation.getId());
            return;
        }

        paymentService.cancelPayment(reservation.getId());
        reservationRepository.deleteById(reservation.getId());
    }
}
