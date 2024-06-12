package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.member.Member;
import roomescape.domain.repository.*;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationDate;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.ReservationWaiting;
import roomescape.domain.reservation.theme.Theme;
import roomescape.service.request.ReservationWaitingSaveDto;
import roomescape.service.response.ReservationWaitingDto;
import roomescape.service.response.ReservationWaitingWithRankDto;
import roomescape.web.exception.AuthorizationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationWaitingService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;

    public ReservationWaitingService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository,
            ReservationWaitingRepository reservationWaitingRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
    }

    public ReservationWaitingDto save(ReservationWaitingSaveDto request) {
        ReservationWaiting waiting = createWaiting(request);
        validateWaiting(waiting);
        ReservationWaiting savedWaiting = reservationWaitingRepository.save(waiting);
        return new ReservationWaitingDto(savedWaiting);
    }

    private ReservationWaiting createWaiting(ReservationWaitingSaveDto request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("예약 대기 생성 실패: 사용자를 찾을 수 없습니다 (id: %d)", request.memberId())));
        ReservationDate date = new ReservationDate(request.date());
        ReservationTime time = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("예약 대기 생성 실패: 시간을 찾을 수 없습니다 (id: %d)", request.timeId())));
        Theme theme = themeRepository.findById(request.timeId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("예약 대기 생성 실패: 테마를 찾을 수 없습니다 (id: %d)", request.themeId())));

        return new ReservationWaiting(LocalDateTime.now(), member, date, time, theme);
    }

    private void validateWaiting(ReservationWaiting waiting) {
        validateReservationExist(waiting);
        validateNotDuplicated(waiting);
    }

    private void validateReservationExist(ReservationWaiting waiting) {
        ReservationDate date = waiting.getDate();
        Long timeId = waiting.getTime().getId();
        Long themeId = waiting.getTheme().getId();
        Reservation reservation = reservationRepository.findByDateAndTimeIdAndThemeId(date, timeId, themeId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "예약이 존재하지 않는 날짜, 시간, 테마에 대해서는 대기를 생성할 수 없습니다. {date: %s, timeId: %d, themeId: %d}",
                        date.getDate(), timeId, themeId)));

        if (waiting.hasSameMemberWith(reservation)) {
            throw new IllegalArgumentException(String.format(
                    "본인이 예약한 날짜, 시간, 테마에 대해서는 대기를 생성할 수 없습니다. {date: %s, timeId: %d, themeId: %d}",
                    date.getDate(), timeId, themeId));
        }
    }

    private void validateNotDuplicated(ReservationWaiting waiting) {
        Member member = waiting.getMember();
        ReservationDate date = waiting.getDate();
        ReservationTime time = waiting.getTime();
        Theme theme = waiting.getTheme();
        boolean isWaitingExist = reservationWaitingRepository.existsByMemberAndDateAndTimeAndTheme(member, date, time, theme);

        if (isWaitingExist) {
            throw new IllegalArgumentException(String.format(
                    "동일한 사용자의 중복된 예약 대기를 생성할 수 없습니다. {date: %s, timeId: %d, themeId: %d}",
                    date.getDate(), time.getId(), theme.getId()));
        }
    }

    public List<ReservationWaitingWithRankDto> findWaitingWithRankByMemberId(Long memberId) {
        return reservationWaitingRepository.findAllWaitingWithRankByMemberId(memberId)
                .stream()
                .map(ReservationWaitingWithRankDto::new)
                .toList();
    }

    public void deleteMemberWaiting(Long memberId, Long waitingId) {
        ReservationWaiting waiting = reservationWaitingRepository.findById(waitingId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("사용자 예약 대기 삭제 실패: 대기를 찾을 수 없습니다. (id: %d)", waitingId)));
        if (!waiting.hasMemberId(memberId)) {
            throw new AuthorizationException(String.format("예약 대기 삭제 권한이 없는 사용자입니다. (id: %d)", memberId));
        }
        reservationWaitingRepository.deleteById(waitingId);
    }

    public List<ReservationWaitingDto> findAllAllowed() {
        return reservationWaitingRepository.findAll()
                .stream()
                .filter(ReservationWaiting::isAllowed)
                .map(ReservationWaitingDto::new)
                .toList();
    }

    public ReservationWaitingDto denyWaiting(Long waitingId) {
        ReservationWaiting waiting = reservationWaitingRepository.findById(waitingId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("예약 대기 상태 변경 실패: 대기를 찾을 수 없습니다. (id: %d)", waitingId)));
        waiting.setDeniedAt(LocalDateTime.now());
        ReservationWaiting updatedWaiting = reservationWaitingRepository.save(waiting);
        return new ReservationWaitingDto(updatedWaiting);
    }

    public boolean existsByReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("존재하지 않는 예약입니다. (id: %d)", reservationId)));
        ReservationDate date = reservation.getDate();
        ReservationTime time = reservation.getTime();
        Theme theme = reservation.getTheme();
        return reservationWaitingRepository.existsByDateAndTimeAndTheme(date, time, theme);
    }
}
