package roomescape.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.model.Reservation;

import java.time.LocalDate;

public class ReservationResponse {

    @Schema(description = "예약 ID", example = "1")
    private Long id;
    @Schema(description = "예약자 이름", example = "수달")
    private String name;
    @Schema(description = "예약 날짜", example = "2024-06-11")
    private LocalDate date;
    @Schema(description = "예약 시간")
    private ReservationTimeResponse time;
    @Schema(description = "예약 테마")
    private ReservationThemeResponse theme;

    private ReservationResponse() {
    }

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.name = reservation.getMember().getName();
        this.date = reservation.getDate();
        this.time = ReservationTimeResponse.of(reservation.getTime());
        this.theme = ReservationThemeResponse.of(reservation.getTheme());
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
