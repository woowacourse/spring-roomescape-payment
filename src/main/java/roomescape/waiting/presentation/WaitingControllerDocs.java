package roomescape.waiting.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.common.argumentResolver.Login;
import roomescape.member.dto.request.LoginMember;
import roomescape.waiting.dto.request.WaitingRequest;
import roomescape.waiting.dto.response.WaitingResponse;

import java.util.List;

@Tag(name = "대기", description = "대기 관련 API")
public interface WaitingControllerDocs {

    @Operation(summary = "대기 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "대기 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = {
                    @ExampleObject(
                            name = "이미 대기 중인 예약",
                            value = "{\"message\": \"이미 대기 중인 예약이 있습니다.\"}"
                    )
            })),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/waitings")
    ResponseEntity<WaitingResponse> createWaiting(
            @RequestBody final WaitingRequest request,
            @Login final LoginMember loginMember
    );

    @Operation(summary = "대기 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대기 목록 조회 성공")
    })
    @GetMapping("/waitings")
    ResponseEntity<List<WaitingResponse>> getWaitings();

    @Operation(summary = "대기 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "대기 취소 성공"),
            @ApiResponse(responseCode = "404", description = "대기를 찾을 수 없음", content = @Content(examples = {
                    @ExampleObject(
                            name = "존재하지 않는 대기",
                            value = "{\"message\": \"해당 대기를 찾을 수 없습니다.\"}"
                    )
            }))
    })
    @DeleteMapping("/waitings/{id}")
    ResponseEntity<Void> cancelWaiting(@PathVariable Long id);
} 