package roomescape.wait.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "예약 대기 생성 요청 정보")
public record CreateReservationWaitRequest(

        @Schema(description = "예약 날짜", example = "2025-07-01")
        @NotNull
        LocalDate date,

        @Schema(description = "예약 시간 ID", example = "3")
        @NotNull
        Long time,

        @Schema(description = "테마 ID", example = "2")
        @NotNull
        Long theme

) {}
