package roomescape.time.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "예약 가능한 시간 조회 요청 정보")
public record AvailableReservationTimeRequest(

        @Schema(description = "예약 날짜", example = "2025-07-01")
        @NotNull
        LocalDate date,

        @Schema(description = "테마 ID", example = "2")
        @NotNull
        Long themeId

) {}
