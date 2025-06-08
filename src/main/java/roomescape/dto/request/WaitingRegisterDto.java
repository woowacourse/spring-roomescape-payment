package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "웨이팅 저장 요청 DTO")
public record WaitingRegisterDto(
        @Schema(description = "웨이팅을 등록하고자 하는 테마의 ID")
        @NotNull
        Long theme,

        @Schema(description = "웨이팅을 등록하고자 하는 시각의 ID")
        @NotNull
        Long time,

        @Schema(description = "웨이팅을 등록하고자 하는 날짜", example = "2022-10-30")
        @NotNull
        LocalDate date
) {
}
