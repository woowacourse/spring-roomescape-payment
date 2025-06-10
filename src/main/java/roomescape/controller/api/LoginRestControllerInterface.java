package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.domain.auth.dto.LoginCheckResponse;
import roomescape.domain.auth.dto.LoginRequest;
import roomescape.domain.auth.dto.LoginResponse;

@Tag(name = "Login", description = "로그인 관련 API")
@RequestMapping("/login")
public interface LoginRestControllerInterface {

    @Operation(summary = "로그인 요청", description = "사용자가 로그인을 요청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "404", description = "회원 정보가 없어 토큰 발급 실패"),
    })
    @PostMapping
    ResponseEntity<LoginResponse> login(
            @RequestBody final LoginRequest loginRequest,
            final HttpServletResponse response
    );

    @Operation(summary = "로그인 확인", description = "로그인을 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 확인 성공"),
            @ApiResponse(responseCode = "401", description = "인증이 되지 않았습니다."),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없습니다."),
    })
    @GetMapping("/check")
    ResponseEntity<LoginCheckResponse> checkLogin(final HttpServletRequest request);
}
