package roomescape.dto.request;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Theme;

@Schema(description = "Theme Request Model")
public record ThemeRequest(@Schema(description = "Theme name", example = "Haunted House")
                           String name,

                           @Schema(description = "Theme description", example = "A scary haunted house theme")
                           String description,

                           @Schema(description = "Thumbnail URL", example = "http://example.com/thumbnail.jpg")
                           String thumbnail) {

    public ThemeRequest {
        isValid(name, description, thumbnail);
    }

    public Theme toEntity() {
        return new Theme(name, description, thumbnail);
    }

    private void isValid(String name, String description, String thumbnail) {
        validEmpty(name);
        validEmpty(description);
        validEmpty(thumbnail);
        validThumbnailURL(thumbnail);
    }

    private void validEmpty(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 테마 등록 시 빈 값은 허용하지 않습니다");
        }
    }

    private void validThumbnailURL(String thumbnail) {
        String regex = "^(https?|ftp|file)://.+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(thumbnail);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("[ERROR] 썸네일 URL 형식이 올바르지 않습니다");
        }
    }
}
