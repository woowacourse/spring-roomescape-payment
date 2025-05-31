package roomescape.presentation.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateReservationAdminRequest(
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "예약 날짜를 선택해주세요.")
        LocalDate date,

        @NotNull(message = "예약 시간을 선택해주세요.")
        Long timeId,

        @NotNull(message = "테마를 선택해주세요.")
        Long themeId,

        @NotNull(message = "사용자 정보를 입력해주세요.")
        Long userId
) {
}
