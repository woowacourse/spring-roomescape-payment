package roomescape.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import roomescape.auth.dto.Accessor;
import roomescape.auth.dto.LoginCheckResponse;
import roomescape.auth.dto.LoginRequest;

@Tag(name = "인증", description = "인증 API")
public interface AuthControllerDocs {

    @Operation(summary = "로그인")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "로그인 실패"),
        @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @Parameters({
        @Parameter(name = "email", description = "최대 길이 320 글자", required = true),
        @Parameter(name = "password", description = "최소 6글자, 최대 20글자", required = true)
    })
    public ResponseEntity<Void> login(LoginRequest loginRequest, HttpServletResponse response);

    @Operation(summary = "로그인 체크")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 체크 성공"),
        @ApiResponse(responseCode = "400", description = "로그인 체크 실패"),
        @ApiResponse(responseCode = "401", description = "로그인 체크 실패")
    })
    public ResponseEntity<LoginCheckResponse> checkLogin(Accessor accessor);

    @Operation(summary = "로그아웃")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response);
}
