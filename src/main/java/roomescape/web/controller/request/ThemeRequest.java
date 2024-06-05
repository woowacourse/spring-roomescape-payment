package roomescape.web.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ThemeRequest(
        @NotBlank(message = "잘못된 테마 이름을 입력하셨습니다.") String name,
        @NotBlank(message = "잘못된 테마 설명을 입력하셨습니다.") String description,
        @NotBlank(message = "잘못된 형식의 썸네일 url입니다.") String thumbnail,
        @NotNull(message = "가격이 비어 있습니다.") Long price
        ) {
}
