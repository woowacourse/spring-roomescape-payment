package roomescape.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import roomescape.auth.domain.AuthInfo;
import roomescape.auth.dto.request.LoginRequest;
import roomescape.auth.dto.response.GetAuthInfoResponse;

@Tag(name = "회원 인증 API")
public interface AuthControllerApi {
    @Operation(
            summary = "로그인",
            description = "회원/어드민 계정으로 로그인",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "400", content = {@Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "올바르지 않은 이메일 형식",
                                            description = "이메일 형식이 아닌 이메일인 경우",
                                            value = "detail: 이메일은 메일 형식만 가능합니다."
                                    ), @ExampleObject(
                                    name = "공백인 이메일",
                                    description = "이메일 값으로 공백이 들어온 경우",
                                    value = "detail: 이메일은 공백 문자가 불가능합니다."
                            ), @ExampleObject(
                                    name = "공백인 비밀번호",
                                    description = "비밀번호 값으로 공백이 들어온 경우",
                                    value = "detail: 비밀번호는 공백 문자가 불가능합니다.")})}),
                    @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 계정",
                                            description = "해당 이메일에 대한 회원이 서버에 존재하지 않는 경우",
                                            value = "detail: 이메일 abc@naver.com에 해당하는 회원이 존재하지 않습니다."
                                    ), @ExampleObject(
                                    name = "아이디 또는 비밀번호 오류",
                                    description = "비밀번호가 틀린 경우",
                                    value = "detail: 아이디 또는 비밀번호를 잘못 입력했습니다. 다시 입력해주세요.")}))},
            parameters = @Parameter(name = "loginMemberRequest", description = "로그인 정보를 담은 객체", required = true))
    ResponseEntity<Void> login(HttpServletResponse httpServletResponse, LoginRequest loginMemberRequest);

    @SecurityRequirement(name = "쿠키 인증 토큰")
    @Operation(
            summary = "회원 정보 조회",
            description = "회원/어드민 계정으로 로그인한 정보 조회합니다. 현재는 회원의 이름만을 반환합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정보 조회 성공", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetAuthInfoResponse.class))),
                    @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "존재하지 않는 회원",
                                    description = "조회하려는 회원의 식별자에 대한 정보가 서버에 존재하지 않는 경우.",
                                    value = "detail: 식별자 1에 해당하는 회원이 존재하지 않습니다.")))})
    ResponseEntity<GetAuthInfoResponse> getMemberAuthInfo(AuthInfo authInfo);

    @Operation(
            summary = "로그아웃",
            description = "회원/어드민 계정으로 로그아웃",
            responses = @ApiResponse(responseCode = "204", description = "로그아웃 성공"))
    ResponseEntity<Void> logout(HttpServletResponse httpServletResponse);
}
