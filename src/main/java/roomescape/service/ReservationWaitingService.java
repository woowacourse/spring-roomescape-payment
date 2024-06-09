package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.reservationwaiting.ReservationWaitingRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.AccessDeniedException;
import roomescape.service.dto.request.WaitingAcceptRequest;
import roomescape.service.dto.request.WaitingCreateRequest;
import roomescape.service.dto.response.ReservationResponse;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class ReservationWaitingService {

    private static final int MAX_RESERVATION_WAITING_COUNT = 10;

    private final ReservationWaitingRepository reservationWaitingRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final Clock clock;

    public ReservationWaitingService(ReservationWaitingRepository reservationWaitingRepository,
                                     ReservationRepository reservationRepository,
                                     ReservationTimeRepository reservationTimeRepository,
                                     ThemeRepository themeRepository,
                                     MemberRepository memberRepository,
                                     PaymentRepository paymentRepository,
                                     Clock clock) {
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
        this.clock = clock;
    }

    @Transactional
    public ReservationResponse addReservationWaiting(WaitingCreateRequest request) {
        Reservation reservation = getReservation(request);
        Member waitingMember = getMember(request.memberId());
        reservation.validateOwnerNotSameAsWaitingMember(waitingMember);
        List<ReservationWaiting> reservationWaitings = reservationWaitingRepository.findAllByReservation(reservation);
        validateWaitingCount(reservationWaitings);
        validateAlreadyWaitingMember(reservationWaitings, waitingMember);
        ReservationWaiting reservationWaiting = reservationWaitingRepository.save(
                request.toReservationWaiting(reservation, waitingMember));
        reservationWaiting.validateFutureReservationWaiting(LocalDateTime.now(clock));
        return ReservationResponse.from(reservationWaiting);
    }

    private Reservation getReservation(WaitingCreateRequest request) {
        ReservationTime time = getTime(request.timeId());
        Theme theme = getTheme(request.themeId());
        return reservationRepository.findByDateAndTimeAndThemeAndStatusIs(request.date(), time, theme, ReservationStatus.ACCEPTED)
                .orElseThrow(() -> new NoSuchElementException("예약이 존재하지 않습니다."));
    }

    private ReservationTime getTime(long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약 시간입니다."));
    }

    private Theme getTheme(long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 테마입니다."));
    }

    private Member getMember(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
    }

    private void validateWaitingCount(List<ReservationWaiting> reservationWaitings) {
        if (reservationWaitings.size() >= MAX_RESERVATION_WAITING_COUNT) {
            throw new IllegalArgumentException("예약 대기열이 가득 찼습니다.");
        }
    }

    private void validateAlreadyWaitingMember(List<ReservationWaiting> reservationWaitings, Member member) {
        reservationWaitings.stream()
                .filter(reservationWaiting -> reservationWaiting.isSameMember(member))
                .findAny()
                .ifPresent(reservationWaiting -> {
                    throw new IllegalArgumentException("현재 멤버는 이미 예약 대기 중입니다.");
                });
    }

    @Transactional
    public ReservationResponse acceptReservationWaiting(WaitingAcceptRequest request) {
        ReservationWaiting reservationWaiting = reservationWaitingRepository.findById(request.waitingId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약 대기입니다."));
        Member member = getMember(request.memberId());
        if (member.isNotAdmin()) {
            throw new AccessDeniedException("관리자만 예약 대기를 확정할 수 있습니다.");
        }
        Reservation reservation = reservationWaiting.getReservation();
        if (!reservation.isCanceled()) {
            throw new IllegalArgumentException("기존 예약이 취소되어야 예약 대기를 확정할 수 있습니다.");
        }
        reservation.changeMember(reservationWaiting.getMember());
        reservationWaitingRepository.delete(reservationWaiting);
        paymentRepository.save(Payment.accountTransfer(request.accountNumber(), request.accountHolder(), request.bankName(), request.amount(), reservation));
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public void deleteReservationWaiting(long waitingId, long memberId) {
        ReservationWaiting reservationWaiting = reservationWaitingRepository.findById(waitingId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약 대기입니다."));
        Member member = getMember(memberId);
        if (member.isNotAdmin()) {
            reservationWaiting.validateOwner(member);
        }
        reservationWaitingRepository.delete(reservationWaiting);
    }

    public List<ReservationResponse> getReservationWaitings() {
        return reservationWaitingRepository.findAll()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
