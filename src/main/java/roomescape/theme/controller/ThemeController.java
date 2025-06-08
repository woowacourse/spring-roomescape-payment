package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.reservation.controller.dto.CreateReservationWebRequest;
import roomescape.theme.controller.dto.CreateThemeWebRequest;
import roomescape.theme.controller.dto.ThemeWebResponse;

public interface ThemeController {

    @Tag(name = "테마 API")
    @Operation(summary = "테마 목록 조회", security = @SecurityRequirement(name = "loginAuth"))
    List<ThemeWebResponse> getAll();

    @Tag(name = "테마 API")
    @Operation(
            summary = "인기 테마 목록 조회",
            description = "최근 일주일 예약 기준 상위 10개 테마",
            security = @SecurityRequirement(name = "loginAuth")
    )
    ResponseEntity<List<ThemeWebResponse>> getRanking();

    @Tag(name = "테마 API [관리자 권한]")
    @Operation(summary = "테마 생성", security = @SecurityRequirement(name = "loginAuth"))
    @RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = CreateThemeWebRequest.class),
                    examples = {
                            @ExampleObject(name = "sample",
                                    value = "{\"name\":\"공포방탈출\",\"description\":\"진짜 ㄹㅇ 무서운 테마\",\"thumbnail\":\"테마썸네일.com\"}")
                    }
            )
    )
    ResponseEntity<ThemeWebResponse> create(CreateThemeWebRequest createThemeWebRequest);

    @Tag(name = "테마 API [관리자 권한]")
    @Operation(summary = "테마 삭제", security = @SecurityRequirement(name = "loginAuth"))
    ResponseEntity<Void> delete(Long id);
}
