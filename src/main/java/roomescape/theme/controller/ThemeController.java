package roomescape.theme.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import roomescape.theme.controller.api.ThemeApi;
import roomescape.theme.dto.request.ThemeRequest;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.service.ThemeService;

@RequiredArgsConstructor
@RestController
public class ThemeController implements ThemeApi {

    private final ThemeService themeService;

    @Override
    public ResponseEntity<List<ThemeResponse>> readAllThemes() {
        List<ThemeResponse> responses = themeService.getAll();

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<List<ThemeResponse>> readPopularThemes() {
        List<ThemeResponse> responses = themeService.getPopularThemes();

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<ThemeResponse> create(@Valid @RequestBody final ThemeRequest request) {
        ThemeResponse response = themeService.create(request);

        return ResponseEntity.created(URI.create("/themes/" + response.id()))
                .body(response);
    }

    @Override
    public ResponseEntity<Void> delete(final Long themeId) {
        themeService.delete(themeId);

        return ResponseEntity.noContent().build();
    }

}
