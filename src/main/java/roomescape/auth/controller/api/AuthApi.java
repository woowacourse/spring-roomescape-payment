package roomescape.auth.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import roomescape.auth.dto.LoginCheckResponse;
import roomescape.auth.dto.LoginMember;
import roomescape.auth.dto.LoginRequest;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

    @Operation(summary = "로그인", description = "로그인 요청을 처리한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "404", description = "요청에 맞는 사용자가 존재하지 않음")
    })
    @PostMapping("/login")
    ResponseEntity<Void> createToken(@RequestBody @Valid final LoginRequest request);

    @Operation(summary = "로그인 확인", description = "로그인된 상태인지 확인한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 확인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 정보 없음")
    })
    @GetMapping("/login/check")
    ResponseEntity<LoginCheckResponse> checkLogin(final LoginMember member);

    @Operation(summary = "로그아웃", description = "로그아웃 요청을 처리한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    @PostMapping("/logout")
    ResponseEntity<Void> logout();
}
