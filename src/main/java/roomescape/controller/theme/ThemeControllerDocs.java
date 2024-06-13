package roomescape.controller.theme;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import roomescape.dto.theme.ThemeResponse;

public interface ThemeControllerDocs {

    @Operation(summary = "인기 테마 조회")
    @ApiResponse(responseCode = "200", description = "인기 테마 조회 성공")
    ResponseEntity<List<ThemeResponse>> findAllPopular();
}
