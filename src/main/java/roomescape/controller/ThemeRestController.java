package roomescape.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.api.ThemeRestControllerInterface;
import roomescape.domain.theme.dto.ThemeRequest;
import roomescape.domain.theme.dto.ThemeResponse;
import roomescape.domain.theme.service.ThemeServiceFacade;

@RequiredArgsConstructor
@RestController
public class ThemeRestController implements ThemeRestControllerInterface {

    private final ThemeServiceFacade themeService;

    @Override
    public ResponseEntity<ThemeResponse> createTheme(
            @RequestBody final ThemeRequest themeRequest
    ) {
        final ThemeResponse response = themeService.saveTheme(themeRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteTheme(
            @PathVariable final Long id
    ) {
        themeService.deleteTheme(id);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ThemeResponse>> getThemes() {
        final List<ThemeResponse> themeResponses = themeService.getThemes();

        return ResponseEntity.ok(themeResponses);
    }

    @Override
    public ResponseEntity<List<ThemeResponse>> getPopularThemes() {
        final List<ThemeResponse> popularThemes = themeService.getPopularThemes();

        return ResponseEntity.ok(popularThemes);
    }
}
