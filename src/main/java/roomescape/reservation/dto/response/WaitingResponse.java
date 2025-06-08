package roomescape.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.entity.Waiting;

public record WaitingResponse(
        Long id,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime startAt,
        String memberName,
        String themeName
) {
    public static WaitingResponse from(Waiting waiting) {
        return new WaitingResponse(
                waiting.getId(),
                waiting.getDate(),
                waiting.getTime().getStartAt(),
                waiting.getMember().getName(),
                waiting.getTheme().getName()
        );
    }
}
