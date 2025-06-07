package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.util.DateTime;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.dto.request.ReservationConditionRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.MyReservationAndWaitingResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.reservationTime.domain.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ReservationService {

    private final DateTime dateTime;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;

    public ReservationService(final DateTime dateTime, final ReservationRepository reservationRepository, final ReservationTimeRepository reservationTimeRepository, final ThemeRepository themeRepository, final MemberRepository memberRepository, final WaitingRepository waitingRepository) {
        this.dateTime = dateTime;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
    }

    @Transactional
    public ReservationResponse createPendingReservation(final ReservationRequest request, final Long memberId) {
        ReservationTime time = findReservationTime(request.timeId());
        Theme theme = findTheme(request.themeId());
        Member findMember = findMember(memberId);

        if (reservationRepository.existsByDateAndTimeStartAtAndThemeId(
                request.date(),
                time.getStartAt(),
                theme.getId()
        )) {
            throw new IllegalArgumentException("이미 예약이 존재합니다.");
        }

        Reservation reservation = Reservation.createPendingWithoutId(dateTime.now(), findMember, request.date(), time, theme, null);
        Reservation saveReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(saveReservation);
    }

    private ReservationTime findReservationTime(final long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시간입니다."));
    }

    private Theme findTheme(final long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테마입니다."));
    }

    private Member findMember(final long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(final ReservationConditionRequest request) {
        if (request.isEmpty()) {
            return reservationRepository.findAll().stream()
                    .map(ReservationResponse::from)
                    .toList();
        }
        return reservationRepository.findByMemberIdAndThemeIdAndDate(request.memberId(), request.themeId(),
                        request.dateFrom(), request.dateTo())
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void deleteReservationById(final Long id) {
        Reservation reservation = findReservation(id);

        reservationRepository.deleteById(id);

        List<Waiting> waitings = waitingRepository.findByDateAndThemeIdAndTimeIdOrderByCreatedAtAsc(
                reservation.getDate(),
                reservation.getThemeId(),
                reservation.getTimeId()
        );

        if (!waitings.isEmpty()) {
            approveWaiting(waitings);
        }
    }

    private Reservation findReservation(final Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
    }

    private void approveWaiting(final List<Waiting> waitings) {
        Waiting firstWaiting = waitings.get(0);

        Reservation newReservation = Reservation.createPendingWithoutId(
                dateTime.now(),
                firstWaiting.getMember(),
                firstWaiting.getDate(),
                firstWaiting.getTime(),
                firstWaiting.getTheme(),
                null
        );
        reservationRepository.save(newReservation);

        waitingRepository.delete(firstWaiting);
    }

    @Transactional(readOnly = true)
    public List<MyReservationAndWaitingResponse> getMyReservations(final Long id) {
        List<Reservation> confirmedReservations = reservationRepository.findByMemberId(id);
        List<MyReservationAndWaitingResponse> confirmedResponses = confirmedReservations.stream()
                .map(MyReservationAndWaitingResponse::from)
                .toList();

        List<Waiting> waitingReservations = waitingRepository.findByMemberId(id);
        List<MyReservationAndWaitingResponse> waitingResponses = waitingReservations.stream()
                .map(waiting -> {
                    long rank = calculateWaitingRank(waiting);
                    return MyReservationAndWaitingResponse.fromWaiting(waiting, rank);
                })
                .toList();

        return Stream.concat(confirmedResponses.stream(), waitingResponses.stream())
                .toList();
    }

    private long calculateWaitingRank(final Waiting waiting) {
        return waitingRepository.countByDateAndThemeIdAndTimeIdAndCreatedAtBefore(
                waiting.getDate(),
                waiting.getTheme().getId(),
                waiting.getTime().getId(),
                waiting.getCreatedAt()
        ) + 1;
    }
}
