package roomescape.waiting;

import org.springframework.stereotype.Service;
import roomescape.member.LoginMember;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;

import java.util.List;

@Service
public class WaitingService {
    private final WaitingRepository waitingRepository;
    private final TimeRepository timeRepository;
    private final ThemeRepository themeRepository;

    public WaitingService(WaitingRepository waitingRepository, TimeRepository timeRepository, ThemeRepository themeRepository) {
        this.waitingRepository = waitingRepository;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
    }

    public WaitingResponse createWaiting(LoginMember loginMember, WaitingRequest waitingRequest) {
        Time time = timeRepository.findById(waitingRequest.getTime()).orElseThrow(RuntimeException::new);
        Theme theme = themeRepository.findById(waitingRequest.getTheme()).orElseThrow(RuntimeException::new);

        List<Waiting> waitings = waitingRepository.findByThemeIdAndDateAndTime(waitingRequest.getTheme(), waitingRequest.getDate(), time.getValue());

        waitings.stream()
                .filter(it -> it.isOwner(loginMember.getMemberId()))
                .filter(it -> it.getTheme().getId() == waitingRequest.getTheme())
                .filter(it -> it.getDate().equals(waitingRequest.getDate()))
                .filter(it -> it.getTime().equals(waitingRequest.getTime()))
                .findAny()
                .ifPresent(it -> {
                    throw new IllegalArgumentException("이미 대기 중인 시간입니다.");
                });

        Waiting waiting = waitingRepository.save(new Waiting(theme, loginMember.getMemberId(), waitingRequest.getDate(), time.getValue()));

        return new WaitingResponse(waiting.getId(), theme.getId(), waiting.getDate(), waiting.getTime(), waitings.size());
    }

    public void deleteWaiting(Long id, LoginMember loginMember) {
        Waiting waiting = waitingRepository.findById(id).orElseThrow(RuntimeException::new);
        if (!waiting.isOwner(loginMember.getMemberId())) {
            throw new IllegalArgumentException("삭제할 권한이 없습니다.");
        }
        waitingRepository.deleteById(id);
    }
}
