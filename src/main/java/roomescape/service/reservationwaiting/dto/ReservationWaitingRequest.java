package roomescape.service.reservationwaiting.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ReservationWaitingRequest {
    @NotNull(message = "date 값이 null일 수 없습니다.")
    private final LocalDate date;
    @NotNull(message = "timeId 값이 null일 수 없습니다.")
    private final Long timeId;
    @NotNull(message = "themeId 값이 null일 수 없습니다.")
    private final Long themeId;

    public ReservationWaitingRequest(String date, String timeId, String themeId) {
        this.date = LocalDate.parse(date);
        this.timeId = Long.parseLong(timeId);
        this.themeId = Long.parseLong(themeId);
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getTimeId() {
        return timeId;
    }

    public Long getThemeId() {
        return themeId;
    }
}
