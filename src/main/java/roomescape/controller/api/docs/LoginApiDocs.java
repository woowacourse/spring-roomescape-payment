package roomescape.controller.api.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.request.LoginRequest;
import roomescape.controller.dto.response.ErrorMessageResponse;
import roomescape.controller.dto.response.LoginCheckResponse;
import roomescape.domain.member.Member;

@Tag(name = "Login", description = "로그인 관련 API")
public interface LoginApiDocs {
    @Operation(summary = "로그인 확인 및 정보 조회", description = "로그인 정보를 확인하고 조회할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = LoginCheckResponse.class))}),
            @ApiResponse(responseCode = "401", description = "접근 권한이 없어서 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<LoginCheckResponse> checkLogin(Member member);

    @Operation(summary = "로그인", description = "로그인 할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "401", description = "로그인 정보가 잘못되서 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<Void> login(LoginRequest request);
}
