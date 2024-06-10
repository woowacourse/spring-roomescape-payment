package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record MemberReservationAddRequest(

        @Schema(description = "예약 날짜", example = "2099-12-31")
        @NotNull(message = "예약 날짜는 필수 입니다.")
        LocalDate date,

        @Schema(description = "예약 시간 id", example = "1")
        @NotNull(message = "예약 시간 선택은 필수 입니다.")
        @Positive
        Long timeId,

        @Schema(description = "예약 테마 id", example = "1")
        @NotNull(message = "테마 선택은 필수 입니다.")
        @Positive
        Long themeId
) {
}
