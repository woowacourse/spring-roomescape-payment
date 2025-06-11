package roomescape.reservation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "관리자가 예약을 생성하기 위한 요청 정보")
public record AdminCreateReservationRequest(

        @Schema(description = "예약 날짜", example = "2025-07-01")
        @NotNull
        LocalDate date,

        @Schema(description = "예약 시간 ID", example = "3")
        @NotNull
        Long timeId,

        @Schema(description = "테마 ID", example = "5")
        @NotNull
        Long themeId,

        @Schema(description = "예약할 멤버 ID", example = "1")
        @NotNull
        Long memberId

) {}
