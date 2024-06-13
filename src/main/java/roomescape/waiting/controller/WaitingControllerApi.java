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
import roomescape.waiting.dto.request.CreateWaitingRequest;
import roomescape.waiting.dto.response.CreateWaitingResponse;
import roomescape.waiting.dto.response.FindWaitingWithRankingResponse;

@SecurityRequirement(name = "쿠키 인증 토큰")
public abstract class WaitingControllerApi {

    @Operation(summary = "방탈출 대기 생성", description = "방탈출에 내 대기를 등록합니다. 이미 예약이 존재하는 경우에만, 대기를 등록할 수 있습니다.", responses = {
            @ApiResponse(responseCode = "201", description = "내 대기 생성 성공", content = @Content(schema = @Schema(implementation = CreateWaitingResponse.class))),
            @ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(name = "대기를 생성하려는 방탈출의 예약이 존재하지 않는 경우, 대기가 아닌 예약을 요청해주세요.", value = "2024-10-11의 time: 1, theme: 1의 예약이 존재하지 않습니다."))),
            @ApiResponse(responseCode = "400", content = @Content(examples = @ExampleObject(name = "이미 동일한 방탈출의 대기가 등록되어 있는 경우.", value = "memberId: 1인 회원이 reservationId: 1인 예약에 대해 이미 대기를 신청했습니다."))),
            @ApiResponse(responseCode = "404", content = @Content(examples = @ExampleObject(name = "대기 등록하려는 회원이 서버에 존재하지 않을 경우.", value = "식별자 1에 해당하는 회원이 존재하지 않습니다.")))})
    abstract ResponseEntity<CreateWaitingResponse> createWaiting(AuthInfo authInfo,
                                                                 CreateWaitingRequest createWaitingRequest);

    @Operation(summary = "방탈출 대기 취소", description = "내 대기를 취소합니다.", responses = {
            @ApiResponse(responseCode = "204", description = "내 대기 취소 성공"),
            @ApiResponse(responseCode = "403", content = @Content(examples = {
                    @ExampleObject(name = "로그인된 유저가 취소하려는 예약의 주인이 아닌 경우.", value = "회원의 권한이 없어, 식별자 1인 예약 대기를 삭제할 수 없습니다.")
            })),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(name = "거절하려는 대기의 식별자 대한 정보가 서버에 존재하지 않는 경우.", value = "식별자 1에 해당하는 예약 대기가 존재하지 않습니다.")
            }))},
            parameters = @Parameter(name = "waitingId", description = "삭제하려는 대기 식별자", required = true, example = "1"))
    abstract ResponseEntity<Void> deleteWaiting(AuthInfo authInfo, Long waitingId);

    @Operation(summary = "내 방탈출 대기 목록 조회", description = "내 방탈출 대기 목록을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "내 대기 목록 조회 성공", content = @Content(schema = @Schema(implementation = FindWaitingWithRankingResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(name = "존재하지 않는 회원", description = "로그인된 회원이 서버에 존재하지 않을 경우에 발생합니다. 토큰이 조작되었거나 회원이 삭제되었을 수 있습니다. 해당 문제가 아니라면 서버에 문의해주세요.", value = "식별자 1에 해당하는 회원 존재하지 않습니다.")
            }))})
    abstract ResponseEntity<List<FindWaitingWithRankingResponse>> getWaitingsByMember(AuthInfo authInfo);
}
