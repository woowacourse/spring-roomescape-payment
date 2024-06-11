package roomescape.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.model.Waiting;

import java.time.LocalDate;

public class WaitingResponse {

    @Schema(description = "예약 대기 ID", example = "1")
    private Long id;
    @Schema(description = "예약 대기자 이름", example = "수달")
    private String name;
    @Schema(description = "예약 대기 날짜", example = "2024-06-11")
    private LocalDate date;
    @Schema(description = "예약 대기 시간")
    private ReservationTimeResponse time;
    @Schema(description = "예약 대기 테마")
    private ReservationThemeResponse theme;

    private WaitingResponse() {
    }

    public WaitingResponse(Waiting waiting) {
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
