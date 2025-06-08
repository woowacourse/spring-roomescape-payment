package roomescape.time.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@Schema(description = "예약 시간 생성 요청 정보")
public record CreateReservationTimeRequest(

        @Schema(description = "예약 시작 시간", example = "14:00")
        @NotNull
        LocalTime startAt

) {}
