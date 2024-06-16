package roomescape.waiting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.waiting.domain.Waiting;

public record WaitingResponse(
        Long id,
        String memberName,
        String themeName,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime startAt) {

    public static WaitingResponse from(Waiting waiting) {
        return new WaitingResponse(
                waiting.getId(),
                waiting.getMember().getName(),
                waiting.getSchedule().getTheme().getName(),
                waiting.getSchedule().getDate(),
                waiting.getSchedule().getTime().getStartAt());
    }
}
