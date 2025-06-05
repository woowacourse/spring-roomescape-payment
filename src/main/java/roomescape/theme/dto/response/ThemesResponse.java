package roomescape.theme.dto.response;

import java.util.List;

public record ThemesResponse(List<ThemeResponse> data
) {
    public static ThemesResponse of(List<ThemeResponse> data) {
        return new ThemesResponse(data);
    }
}
