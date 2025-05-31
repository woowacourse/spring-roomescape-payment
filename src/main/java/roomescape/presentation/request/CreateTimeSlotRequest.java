package roomescape.presentation.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record CreateTimeSlotRequest(
        @JsonFormat(pattern = "HH:mm")
        @NotNull(message = "시간을 입력해주세요.")
        LocalTime startAt
) {
}
