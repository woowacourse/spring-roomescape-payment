package roomescape.service.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AdminReservationRequest {
    @NotNull(message = "date 값이 null일 수 없습니다.")
    private final LocalDate date;
    @NotNull(message = "timeId 값이 null일 수 없습니다.")
    private final Long timeId;
    @NotNull(message = "themeId 값이 null일 수 없습니다.")
    private final Long themeId;
    @NotNull(message = "memberId 값이 null일 수 없습니다.")
    private final Long memberId;

    public AdminReservationRequest(String date, String timeId, String themeId, String memberId) {
        this.date = LocalDate.parse(date);
        this.timeId = Long.parseLong(timeId);
        this.themeId = Long.parseLong(themeId);
        this.memberId = Long.parseLong(memberId);
    }

    public ReservationSaveInput toReservationSaveInput() {
        return new ReservationSaveInput(date, timeId, themeId);
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

    public Long getMemberId() {
        return memberId;
    }
}
