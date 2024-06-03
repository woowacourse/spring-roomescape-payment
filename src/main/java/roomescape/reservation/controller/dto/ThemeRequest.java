package roomescape.reservation.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ThemeRequest(
        @NotBlank(message = "이름을 입력하지 않았습니다.") String name,
        @NotBlank(message = "설명을 입력하지 않았습니다.") String description,
        @NotBlank(message = "썸네일을 입력하지 않았습니다.") String thumbnail) {
}
