package roomescape.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.core.AuthenticationPrincipal;
import roomescape.auth.domain.AuthInfo;
import roomescape.waiting.dto.request.CreateWaitingRequest;
import roomescape.waiting.dto.response.CreateWaitingResponse;
import roomescape.waiting.dto.response.FindWaitingWithRankingResponse;
import roomescape.waiting.service.WaitingService;

import java.net.URI;
import java.util.List;

@Tag(name = "예약 대기 API", description = "예약 대기 관련 API")
@RestController
@RequestMapping
public class WaitingController {

    private final WaitingService waitingService;

    public WaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "예약 대기 생성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 대기 생성 성공"),
            @ApiResponse(responseCode = "400", description = """
                    1. 예약 대기 날짜는 현재보다 과거일 수 없습니다.
                    2. 예약 대기 등록 시 예약 날짜는 필수입니다.
                    3. 예약 대기 시간 식별자는 양수만 가능합니다.
                    4. 예약 대기 등록 시 시간은 필수입니다.
                    5. 예약 대기 테마 식별자는 양수만 가능합니다.
                    6. 예약 대기 등록 시 테마는 필수입니다.
                    7. 회원이 예약에 대해 이미 대기를 신청했습니다.
                    """, content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = """
                    1. 예약이 존재하지 않습니다.
                    2. 식별자에 해당하는 회원이 존재하지 않습니다.
                    """, content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/waitings")
    public ResponseEntity<CreateWaitingResponse> createWaiting(@AuthenticationPrincipal AuthInfo authInfo,
                                                               @Valid @RequestBody CreateWaitingRequest createWaitingRequest) {
        CreateWaitingResponse createWaitingResponse = waitingService.createWaiting(authInfo, createWaitingRequest);
        return ResponseEntity.created(URI.create("/waitings/" + createWaitingResponse.waitingId()))
                .body(createWaitingResponse);
    }

    @Operation(summary = "예약 대기 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "예약 대기 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "회원의 권한이 없어, 식별자의 예약 대기를 삭제할 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "식별자에 해당하는 예약 대기가 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/waitings/{waitingId}")
    public ResponseEntity<Void> deleteWaiting(@AuthenticationPrincipal AuthInfo authInfo,
                                              @PathVariable Long waitingId) {
        waitingService.deleteWaiting(authInfo, waitingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 예약 대기 목록 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 예약 대기 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "식별자에 해당하는 회원 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })

    @GetMapping("/members/waitings")
    public ResponseEntity<List<FindWaitingWithRankingResponse>> getWaitingsByMember(
            @AuthenticationPrincipal AuthInfo authInfo) {
        return ResponseEntity.ok(waitingService.getWaitingsByMember(authInfo));
    }
}
