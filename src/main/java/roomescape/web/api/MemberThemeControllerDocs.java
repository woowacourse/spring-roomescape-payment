package roomescape.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.application.dto.response.theme.ThemeResponse;

@Tag(name = "테마 조회", description = "테마 조회 API")
interface MemberThemeControllerDocs {

    @Operation(summary = "테마 전체 조회", description = "테마 전체를 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "테마 전체 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ThemeResponse.class)),
                            examples = @ExampleObject(value = """
                                    [
                                         {
                                             "id": 1,
                                             "name": "세렌디피티: 뜻밖의 행운",
                                             "description": "행운을 불러오는 테마",
                                             "thumbnail": "https://roomescape.s3.ap-northeast-2.amazonaws.com/theme/thumbnail/serendipity.jpg"
                                         },
                                         {
                                             "id": 2,
                                             "name": "포레스트: 숲속의 비밀",
                                             "description": "숲속의 비밀을 찾아라",
                                             "thumbnail": "https://roomescape.s3.ap-northeast-2.amazonaws.com/theme/thumbnail/forest.jpg"
                                         }
                                    ]
                                    """
                            )
                    )
            )
    })
    ResponseEntity<List<ThemeResponse>> findAllTheme();

    @Operation(summary = "인기 테마 조회", description = "상위 10개 인기 테마를 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "인기 테마 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ThemeResponse.class)),
                            examples = @ExampleObject(value = """
                                    [
                                         {
                                             "id": 1,
                                             "name": "세렌디피티: 뜻밖의 행운",
                                             "description": "행운을 불러오는 테마",
                                             "thumbnail": "https://roomescape.s3.ap-northeast-2.amazonaws.com/theme/thumbnail/serendipity.jpg"
                                         },
                                         {
                                             "id": 2,
                                             "name": "포레스트: 숲속의 비밀",
                                             "description": "숲속의 비밀을 찾아라",
                                             "thumbnail": "https://roomescape.s3.ap-northeast-2.amazonaws.com/theme/thumbnail/forest.jpg"
                                         }
                                    ]
                                    """
                            )
                    )
            )
    })
    ResponseEntity<List<ThemeResponse>> findAllPopularTheme();
}
