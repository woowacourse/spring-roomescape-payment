package roomescape.reservation.waiting.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class WaitingWithRank {

    private final Waiting waiting;
    private final long rank;

    public WaitingWithRank(final Waiting waiting, final long rank) {
        this.waiting = waiting;
        this.rank = rank;
    }

    public Long getId() {
        return waiting.getId();
    }

    public String themeName() {
        return waiting.themeName();
    }

    public LocalDate getDate() {
        return waiting.getDate();
    }

    public LocalTime startTime() {
        return waiting.startTime();
    }

    public Long getRank() {
        return rank;
    }
}
