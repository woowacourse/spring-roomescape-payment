package roomescape.theme.adaptor;

import org.springframework.stereotype.Component;
import roomescape.theme.docs.*;
import roomescape.theme.dto.request.ThemeRequest;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.dto.response.PopularThemeResponse;

@Component
public class ThemeApiAdaptor {

    public ThemeRequest toThemeRequest(ThemeRequestDocs dto) {
        return dto.toThemeRequest();
    }

    public ThemeResponseDocs toThemeResponseDocs(ThemeResponse response) {
        return ThemeResponseDocs.from(response);
    }

    public PopularThemeResponseDocs toPopularThemeResponseDocs(PopularThemeResponse response) {
        return PopularThemeResponseDocs.from(response);
    }
} 