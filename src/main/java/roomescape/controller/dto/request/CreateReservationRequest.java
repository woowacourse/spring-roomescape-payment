package roomescape.controller.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateReservationRequest(
        @Schema(description = "회원 고유 번호", example = "1")
        @NotNull(message = "null일 수 없습니다.")
        @Positive
        Long memberId,

        @Schema(description = "날짜", example = "2024-06-12")
        @NotNull(message = "null일 수 없습니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @FutureOrPresent(message = "과거 날짜로는 예약할 수 없습니다.")
        LocalDate date,

        @Schema(description = "시간 고유 번호", example = "1")
        @NotNull(message = "null일 수 없습니다.")
        @Positive(message = "양수만 입력할 수 있습니다.")
        Long timeId,

        @Schema(description = "테마 고유 번호", example = "1")
        @NotNull(message = "null일 수 없습니다.")
        @Positive(message = "양수만 입력할 수 있습니다.")
        Long themeId
) {
    public static CreateReservationRequest to(Long memberId, CreateUserReservationRequest request) {
        return new CreateReservationRequest(memberId, request.date(), request.timeId(), request.themeId());
    }
}
