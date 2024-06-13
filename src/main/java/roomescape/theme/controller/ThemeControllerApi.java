package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import roomescape.theme.dto.request.CreateThemeRequest;
import roomescape.theme.dto.response.CreateThemeResponse;
import roomescape.theme.dto.response.FindPopularThemesResponse;
import roomescape.theme.dto.response.FindThemeResponse;

public abstract class ThemeControllerApi {

    @Operation(summary = "방탈출 테마 생성", description = "새로운 방탈출 테마를 생성합니다.", responses = {
            @ApiResponse(responseCode = "201", description = "내 테마 생성 성공", content = @Content(schema = @Schema(implementation = CreateThemeResponse.class))),
            @ApiResponse(responseCode = "403", content = @Content(examples = {
                    @ExampleObject(name = "테마 생성 권한 없음", description = "로그인된 유저가 테마를 생성할 수 없는 권한인 경우", value = "detail: 회원의 권한이 없어, 테마를 생성할 수 없습니다.")
            }))},
            parameters = @Parameter(name = "createThemeRequest", description = "생성하려는 테마 정보를 담은 객체", required = true))
    abstract ResponseEntity<CreateThemeResponse> createTheme(CreateThemeRequest createThemeRequest);

    @Operation(summary = "방탈출 테마 목록 조회", description = "전체 테마 목록을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "전체 테마 목록 조회 성공", content = @Content(schema = @Schema(implementation = FindThemeResponse.class)))
    })
    abstract ResponseEntity<List<FindThemeResponse>> getThemes();

    @Operation(summary = "방탈출 인기 테마 조회", description = "예약이 많이 등록된 테마를 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "인기 테마 조회 성공", content = @Content(schema = @Schema(implementation = FindPopularThemesResponse.class)))
    },
            parameters = @Parameter(in = ParameterIn.QUERY, name = "size", description = "페이지 크기", allowEmptyValue = true, schema = @Schema(defaultValue = "10")))
    abstract ResponseEntity<List<FindPopularThemesResponse>> getPopularThemes(Pageable pageable);

    @Operation(summary = "방탈출 테마 삭제", description = "방탈출 테마를 삭제합니다.", responses = {
            @ApiResponse(responseCode = "204", description = "테마 삭제 성공"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(name = "해당 테마에 대한 예약이 존재하는 경우", value = "식별자 1인 테마를 사용 중인 예약이 존재합니다. 삭제가 불가능합니다.")
            })),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(name = "거절하려는 테마의 식별자 대한 정보가 서버에 존재하지 않는 경우", value = "식별자 1에 해당하는 테마가 존재하지 않습니다. 삭제가 불가능합니다.")
            }))},
            parameters = @Parameter(name = "id", description = "삭제하려는 테마 식별자", required = true, example = "1"))
    abstract ResponseEntity<Void> deleteTheme(Long id);
}
