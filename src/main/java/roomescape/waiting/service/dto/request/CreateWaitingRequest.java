package roomescape.waiting.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(name = "CreateWaitingRequest(예약 대기 생성 요청 DTO)")
public record CreateWaitingRequest(
        @NotNull
        LocalDate date,
        @NotNull
        Long themeId,
        @NotNull
        Long timeId
) {
}
