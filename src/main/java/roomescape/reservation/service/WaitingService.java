package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.global.auth.dto.UserInfo;
import roomescape.member.domain.Member;
import roomescape.member.exception.MemberNotFoundException;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.WaitingWithRank;
import roomescape.reservation.exception.WaitingNotFoundException;
import roomescape.reservation.repository.WaitingRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.exception.ReservationTimeNotFoundException;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.exception.ThemeNotFoundException;
import roomescape.theme.repository.ThemeRepository;

@Service
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;

    public WaitingService(final WaitingRepository waitingRepository,
                          final ReservationTimeRepository reservationTimeRepository,
                          final MemberRepository memberRepository, final ThemeRepository themeRepository) {
        this.waitingRepository = waitingRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
    }

    public void delete(Long id) {
        waitingRepository.deleteById(id);
    }

    public List<Waiting> findWaitings() {
        return waitingRepository.findAll();
    }

    public List<WaitingWithRank> findMyWaitingsWithRank(UserInfo userInfo) {
        return waitingRepository.findWaitingsWithRankByMemberId(userInfo.id());
    }

    public int findMaxOrderByDateAndTimeAndTheme(final LocalDate date, final Long timeId, final Long themeId) {
        return waitingRepository.findMaxOrderByDateAndTimeAndTheme(date, timeId, themeId);
    }

    public Waiting save(final Waiting waiting) {
        return waitingRepository.save(waiting);
    }

    public Waiting findFirstWaitingOfInfo(ReservationInfo reservationInfo) {
        return waitingRepository.findFirstByInfoDateAndInfoTimeAndInfoThemeOrderByTurnAsc(reservationInfo.getDate(),
                        reservationInfo.getTime(), reservationInfo.getTheme())
                .orElseThrow(() -> new WaitingNotFoundException("요청한 id와 일치하는 대기 정보가 없습니다."));
    }

    public boolean isWaitingExists(final ReservationInfo info) {
        return waitingRepository.existsByDateAndTimeIdAndThemeId(info.getDate(), info.getIdOfTime(),
                info.getIdOfTheme());
    }

    public Waiting createWaiting(final ReservationRequest request, final Long memberId) {
        ReservationTime time = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> new ReservationTimeNotFoundException("요청한 id와 일치하는 예약 시간 정보가 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("요청한 id와 일치하는 맴버 정보가 없습니다."));
        Theme theme = themeRepository.findById(request.timeId())
                .orElseThrow(() -> new ThemeNotFoundException("요청한 id와 일치하는 테마 정보가 없습니다."));
        ReservationInfo reservationInfo = new ReservationInfo(request.date(), time, theme);
        int turn = waitingRepository.findMaxOrderByDateAndTimeAndTheme(request.date(), request.timeId(),
                request.themeId());
        return waitingRepository.save(
                Waiting.createUpcomingReservationWithUnassignedId(member, turn + 1, reservationInfo));
    }


}
