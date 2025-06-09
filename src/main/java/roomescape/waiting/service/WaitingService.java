package roomescape.waiting.service;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.InvalidReservationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.dto.request.LoginMember;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.reservationTime.domain.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingRepository;
import roomescape.waiting.dto.request.WaitingRequest;
import roomescape.waiting.dto.response.WaitingResponse;

@Service
public class WaitingService {

    private static final Logger log = LoggerFactory.getLogger(WaitingService.class);
    private static final long MAX_WAITING_COUNT = 10;
    private final WaitingRepository waitingRepository;
    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public WaitingService(WaitingRepository waitingRepository, MemberRepository memberRepository, ReservationTimeRepository reservationTimeRepository, ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.waitingRepository = waitingRepository;
        this.memberRepository = memberRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public WaitingResponse createWaiting(WaitingRequest request, LoginMember loginMember) {
        Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> {
                    log.warn("대기 생성 실패 - 회원을 찾을 수 없음 memberId: {}", loginMember.id());
                    return new InvalidReservationException("회원을 찾을 수 없습니다");
                });
        ReservationTime time = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> {
                    log.warn("대기 생성 실패 - 예약 시간을 찾을 수 없음 timeId: {}", request.timeId());
                    return new InvalidReservationException("예약 시간을 찾을 수 없습니다");
                });
        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> {
                    log.warn("대기 생성 실패 - 테마를 찾을 수 없음 themeId: {}", request.themeId());
                    return new InvalidReservationException("테마를 찾을 수 없습니다");
                });

        boolean isBooking = reservationRepository.existsByDateAndTimeStartAtAndThemeId(request.date(), time.getStartAt(), request.themeId());
        if (!isBooking) {
            log.warn("대기 생성 불가 - 해당 날짜/시간에 예약 없음 date: {}, time: {}, themeId: {}", request.date(), time.getStartAt(), request.themeId());
            throw new IllegalStateException("해당 날짜와 시간에 예약이 존재하지 않아 대기를 할 수 없습니다.");
        }

        if (waitingRepository.existsByMemberIdAndDateAndTimeId(
                loginMember.id(),
                request.date(),
                request.timeId()
        )) {
            log.warn("대기 생성 불가 - 이미 대기 존재 memberId: {}, date: {}, timeId: {}", loginMember.id(), request.date(), request.timeId());
            throw new IllegalStateException("해당 날짜와 시간에 이미 대기가 존재합니다.");
        }

        long currentWaitingCounts = waitingRepository.countByDateAndThemeIdAndTimeId(
                request.date(),
                request.themeId(),
                request.timeId()
        );

        if (currentWaitingCounts >= MAX_WAITING_COUNT) {
            log.warn("대기 생성 불가 - 최대 대기 인원 초과 date: {}, themeId: {}, timeId: {}, currentWaitingCount: {}", request.date(), request.themeId(), request.timeId(), currentWaitingCounts);
            throw new IllegalStateException("최대 대기 인원(10명)을 초과했습니다.");
        }
        Waiting waiting = Waiting.createWithoutId(
                member,
                request.date(),
                time,
                theme,
                LocalDateTime.now()
        );
        Waiting savedWaiting = waitingRepository.save(waiting);

        log.info("대기 생성 성공 - waitingId: {}, memberId: {}, date: {}, timeId: {}, themeId: {}", savedWaiting.getId(), loginMember.id(), request.date(), request.timeId(), request.themeId());
        return WaitingResponse.from(savedWaiting);
    }

    @Transactional
    public void cancelWaiting(Long waitingId) {
        Waiting waiting = waitingRepository.findById(waitingId)
                .orElseThrow(() -> {
                    log.warn("대기 취소 실패 - 대기 정보를 찾을 수 없음 waitingId: {}", waitingId);
                    return new InvalidReservationException("대기 정보를 찾을 수 없습니다.");
                });
        waitingRepository.delete(waiting);
    }

    @Transactional(readOnly = true)
    public List<WaitingResponse> getAllWaiting() {
        return waitingRepository.findAll().stream()
                .map(WaitingResponse::from)
                .toList();
    }
}
