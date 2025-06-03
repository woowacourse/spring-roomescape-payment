package roomescape.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Waiting;

public class WaitingWithRank {
    private final Waiting waiting;
    private final Long rank;

    public WaitingWithRank(Waiting waiting, Long rank) {
        this.waiting = waiting;
        this.rank = rank;
    }

    public Waiting getWaiting() {
        return waiting;
    }

    public String getThemeName() {
        return waiting.getThemeName();
    }

    public LocalDate getDate() {
        return waiting.getDate();
    }

    public LocalTime getStartAt() {
        return waiting.getStartAt();
    }

    public Long getWaitingId() {
        return waiting.getId();
    }

    public Long getRank() {
        return rank;
    }
}
