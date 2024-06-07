package roomescape.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.auth.domain.AuthInfo;
import roomescape.waiting.dto.response.FindWaitingResponse;

@SecurityRequirement(name = "쿠키 인증 토큰")
public abstract class AdminWaitingControllerApi {

    @Operation(
            summary = "전체 대기 목록 조회",
            description = "모든 회원의 대기 목록을 조회합니다.",
            responses = @ApiResponse(responseCode = "200", description = "전체 대기 목록 조회 성공", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FindWaitingResponse.class))))
    abstract ResponseEntity<List<FindWaitingResponse>> getWaitings();

    @Operation(
            summary = "방탈출 대기 거절",
            description = "회원의 대기를 거절합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "예약 대기 거절 성공"),
                    @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "거절 권한 없음",
                                    description = "현재 로그인된 유저의 권한으로는 대기를 거절할 수 없는 경우",
                                    value = "detail: 회원의 권한이 없어, 식별자 1인 예약 대기를 삭제할 수 없습니다."))),
                    @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "존재하지 않는 대기",
                                    description = "거절하려는 대기의 식별자 대한 정보가 서버에 존재하지 않는 경우.",
                                    value = "detail: 식별자 1에 해당하는 예약 대기가 존재하지 않습니다.")))},
            parameters = @Parameter(name = "waitingId", description = "거절하려는 대기 식별자", required = true, example = "1"))
    abstract ResponseEntity<Void> rejectWaiting(AuthInfo authInfo, Long waitingId);
}
