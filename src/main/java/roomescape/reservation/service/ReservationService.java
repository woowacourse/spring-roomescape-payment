package roomescape.reservation.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.AdminReservationPaymentRequest;
import roomescape.reservation.dto.MyPageReservationResponse;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.ReservationTheme;
import roomescape.theme.repository.ReservationThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;
import roomescape.waiting.domain.ReservationWaiting;
import roomescape.waiting.repository.ReservationWaitingRepository;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationThemeRepository reservationThemeRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;

    @Transactional
    public ReservationResponse addReservation(long memberId, ReservationPaymentRequest request) {
        Reservation reservation = saveReservation(memberId, request.timeId(), request.themeId(), request.date());
        paymentService.approvePayment(request.orderId(), request.paymentKey(), request.amount(), reservation);
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse addReservationByAdmin(AdminReservationPaymentRequest request) {
        Reservation reservation = saveReservation(request.memberId(), request.timeId(), request.themeId(),
                request.date());
        paymentService.approvePayment(request.orderId(), request.paymentKey(), request.amount(), reservation);
        return ReservationResponse.from(reservation);
    }

    private Reservation saveReservation(Long memberId, Long timeId, Long themeId, LocalDate date) {
        validateDuplicateReservation(date, timeId, themeId);
        Member member = findMemberById(memberId);
        ReservationTime time = findTimeById(timeId);
        ReservationTheme theme = findThemeById(themeId);
        return reservationRepository.save(new Reservation(member, date, time, theme));
    }

    private ReservationTheme findThemeById(long themeId) {
        return reservationThemeRepository.findById(themeId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 테마 입니다."));
    }

    private ReservationTime findTimeById(long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 예약 시간 입니다."));
    }

    private Member findMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 사용자 입니다."));
    }

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> getFilteredReservations(Long memberId, Long themeId,
                                                             LocalDate dateFrom, LocalDate dateTo) {
        List<Reservation> reservations = reservationRepository.findByMemberIdAndThemeIdAndDateFromAndDateTo(
                memberId, themeId, dateFrom, dateTo);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<MyPageReservationResponse> getMyPageReservations(Long memberId) {
        Member member = findMemberById(memberId);
        List<MyPageReservationResponse> responses = new ArrayList<>();
        responses.addAll(getMyReservations(member));
        responses.addAll(getMyWaitings(member));
        return responses;
    }

    private List<MyPageReservationResponse> getMyWaitings(Member member) {
        List<ReservationWaiting> waitings = reservationWaitingRepository.findByMemberId(member.getId());
        return waitings.stream()
                .map(waiting -> MyPageReservationResponse.of(waiting, getWaitingOrderByMember(waiting.getMember())))
                .toList();
    }

    private List<MyPageReservationResponse> getMyReservations(Member member) {
        List<Reservation> reservations = reservationRepository.findByMemberId(member.getId());
        return reservations.stream()
                .map(MyPageReservationResponse::from)
                .collect(Collectors.toList());
    }

    private long getWaitingOrderByMember(Member member) {
        return reservationWaitingRepository.findWaitingOrderById(member.getId());
    }

    @Transactional
    public void removeReservation(long id) {
        validateExistsById(id);
        reservationRepository.findById(id).ifPresent(
                reservation -> {
                    reservationRepository.deleteById(id);
                    convertWaitingToReservation(reservation);
                });
    }

    private void convertWaitingToReservation(Reservation reservation) {
        reservationWaitingRepository.findFirstByThemeIdAndTimeIdAndDateOrderByCreatedAtAsc(
                        reservation.getTheme().getId(), reservation.getTime().getId(), reservation.getDate())
                .ifPresent(reservationWaiting -> {
                    reservationRepository.save(
                            new Reservation(reservationWaiting.getMember(), reservationWaiting.getDate(),
                                    reservationWaiting.getTime(), reservationWaiting.getTheme()));
                    reservationWaitingRepository.deleteById(reservationWaiting.getId());
                });
    }

    private void validateDuplicateReservation(LocalDate localDate, long timeId, long themeId) {
        if (reservationRepository.existByDateAndTimeIdAndThemeId(localDate, timeId, themeId)) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 예약 입니다.");
        }
    }

    private void validateExistsById(long id) {
        if (!reservationRepository.existById(id)) {
            throw new NoSuchElementException("[ERROR] 존재하지 않는 예약 입니다.");
        }
    }
}
