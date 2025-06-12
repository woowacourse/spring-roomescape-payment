package roomescape.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.service.ThemeService;
import roomescape.dto.request.ThemeRegisterDto;
import roomescape.dto.response.ThemeResponseDto;

@Tag(name = "테마 관련 API")
@RestController
@RequestMapping("/themes")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    @Operation(summary = "테마 전체 조회")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ThemeResponseDto> getThemes() {
        return themeService.getAllThemes();
    }

    @Operation(summary = "새로운 테마 저장")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ThemeResponseDto addTheme(@RequestBody @Valid ThemeRegisterDto themeRegisterDto) {
        return themeService.saveTheme(themeRegisterDto);
    }

    @Operation(summary = "테마 삭제")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTheme(@PathVariable("id") Long id) {
        themeService.deleteTheme(id);
    }

    @Operation(summary = "특정 날짜 기준 일주일 간의 인기 테마 조회")
    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<ThemeResponseDto> getPopularThemes(@RequestParam String date) {
        return themeService.findPopularThemes(date);
    }
}
