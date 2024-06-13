package roomescape.reservation.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.domain.Waiting;

public record WaitingResponse(
        long id,
        MemberResponse member,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        ThemeResponse theme,
        TimeResponse time
) {

    public static WaitingResponse toResponse(Waiting waiting) {
        return new WaitingResponse(
                waiting.getId(),
                MemberResponse.toResponse(waiting.getMember()),
                waiting.getDate(),
                ThemeResponse.toResponse(waiting.getTheme()),
                TimeResponse.toResponse(waiting.getTime())
        );
    }
}
