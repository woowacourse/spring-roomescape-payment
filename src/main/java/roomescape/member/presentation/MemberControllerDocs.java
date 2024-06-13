package roomescape.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.member.domain.Member;
import roomescape.member.dto.JoinRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.theme.dto.ThemeResponse;

@Tag(name = "회원", description = "회원 API")
public interface MemberControllerDocs {

    @Operation(summary = "회원 목록 조회", description = "회원 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ThemeResponse.class))))
    public ResponseEntity<List<Member>> readAll();

    @Operation(summary = "회원 추가", description = "회원을 추가합니다.")
    @ApiResponse(responseCode = "201", description = "회원 추가 성공",
            content = @Content(schema = @Schema(implementation = MemberResponse.class)))
    @Parameters({
            @Parameter(name = "email", description = "최대 길이 320 글자", required = true),
            @Parameter(name = "password", description = "최소 6글자, 최대 20글자", required = true),
            @Parameter(name = "name", description = "최대 20글자", required = true)
    })
    public ResponseEntity<MemberResponse> createMember(JoinRequest joinRequest);
}
