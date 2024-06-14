package roomescape.response;

import roomescape.model.Waiting;

import java.time.LocalDate;

public class WaitingResponse {

    private final Long id;
    private final String name;
    private final LocalDate date;
    private final ReservationTimeResponse time;
    private final ReservationThemeResponse theme;

    public WaitingResponse(final Waiting waiting) {
        this.id = waiting.getId();
        this.name = waiting.getMember().getName();
        this.date = waiting.getDate();
        this.time = ReservationTimeResponse.of(waiting.getTime());
        this.theme = ReservationThemeResponse.of(waiting.getTheme());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTimeResponse getTime() {
        return time;
    }

    public ReservationThemeResponse getTheme() {
        return theme;
    }

}
