package roomescape.dto.request;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import roomescape.domain.Theme;

public record ThemeRequest(String name, String description, String thumbnail, BigDecimal price) {

    public ThemeRequest {
        isValid(name, description, thumbnail, price);
    }

    public Theme toEntity() {
        return new Theme(null, name, description, thumbnail, price);
    }

    private void isValid(String name, String description, String thumbnail, BigDecimal price) {
        validEmpty(name);
        validEmpty(description);
        validEmpty(thumbnail);
        validThumbnailURL(thumbnail);
        validPrice(price);
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

    private void validPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.valueOf(1000)) < 0 || price.compareTo(BigDecimal.valueOf(1000000000)) > 0) {
            throw new IllegalArgumentException("[ERROR] 가격이 올바르지 않습니다");
        }
    }
}
