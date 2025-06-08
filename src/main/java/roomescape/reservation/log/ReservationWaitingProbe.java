package roomescape.reservation.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import roomescape.reservation.domain.ReservationWait;

@Slf4j
@Component
public class ReservationWaitingProbe {

    public void create(ReservationWait waiting) {
        log.atInfo().log("create waiting: id={}, date={}, theme={}, time={}, waitingMemberId={}", waiting.getId(),
                waiting.getDate().getValue(), waiting.getTheme().getName().getValue(), waiting.getTime().getStartAt(),
                waiting.getMember().getId());
    }

    public void delete(ReservationWait waiting) {
        log.atInfo().log("delete waiting: id={}, date={}, theme={}, time={}, waitingMemberId={}", waiting.getId(),
                waiting.getDate().getValue(), waiting.getTheme().getName().getValue(), waiting.getTime().getStartAt(),
                waiting.getMember().getId());
    }

    public void promote(ReservationWait waiting) {
        log.atInfo().log("promote waiting: id={}, date={}, theme={}, time={}, waitingMemberId={}", waiting.getId(),
                waiting.getDate().getValue(), waiting.getTheme().getName().getValue(), waiting.getTime().getStartAt(),
                waiting.getMember().getId());
    }
}
