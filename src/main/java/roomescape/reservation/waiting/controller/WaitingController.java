package roomescape.reservation.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.common.exception.dto.ErrorResponse;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.waiting.dto.request.WaitingCreateRequest;
import roomescape.reservation.waiting.dto.response.WaitingResponse;
import roomescape.reservation.waiting.service.WaitingService;

@Tag(name = "예약 대기", description = "예약 대기 API")
@Slf4j
@RequestMapping("/waitings")
@RestController
public class WaitingController {

    private final WaitingService waitingService;

    public WaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(
            summary = "대기 등록",
            description = "이미 예약된 경우 대기를 등록합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "대기 등록 성공",
                    content = @Content(
                            mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<WaitingResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "예약 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ReservationRequest.class))
            )
            @Valid @RequestBody final ReservationRequest request,
            final LoginMember loginMember
    ) {
        log.info("대기 등록 요청: memberId={}, date={}, timeId={}, themeId={}",
                loginMember.id(), request.date(), request.timeId(), request.themeId());
        WaitingCreateRequest createRequest = WaitingCreateRequest.from(request, loginMember);
        WaitingResponse response = waitingService.create(createRequest);
        return ResponseEntity.created(URI.create("/waitings/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "대기 삭제",
            description = "대기 ID를 기준으로 대기를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "대기 삭제 성공",
                    content = @Content(
                            mediaType = "application/json"
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 대기 ID",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{waitingId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 대기 ID", example = "1")
            @PathVariable("waitingId") final Long waitingId
    ) {
        log.info("대기 삭제 요청: waitingId={}", waitingId);
        waitingService.deleteWaiting(waitingId);
        return ResponseEntity.noContent().build();
    }
}
