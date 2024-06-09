package roomescape.application.reservation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.domain.reservation.Theme;

public record ThemeRequest(
        @NotBlank(message = "테마 이름을 입력해주세요.")
        String name,
        @NotBlank(message = "테마 설명을 입력해주세요.")
        String description,
        @NotBlank(message = "썸네일 URL을 입력해주세요.")
        String thumbnail,
        @NotNull(message = "가격을 입력해주세요.")
        Long price) {

    public Theme toTheme() {
        return new Theme(name, description, price, thumbnail);
    }
}