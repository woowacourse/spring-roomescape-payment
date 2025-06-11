package roomescape.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Schema(description = "관리자가 직접 예약 생성 시 요청 객체")
public record CreateReservationRequest(
        @Schema(description = "예약자 회원 ID", example = "1", requiredMode = REQUIRED)
        long memberId,

        @Schema(description = "예약 날짜 (yyyy-MM-dd 형식)", example = "2025-06-15", requiredMode = REQUIRED)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "예약 테마 ID", example = "3", requiredMode = REQUIRED)
        long themeId,

        @Schema(description = "예약 시간 ID", example = "2", requiredMode = REQUIRED)
        long timeId
) {
}
