package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.configuration.annotation.Authority;
import roomescape.configuration.annotation.RequiredAccessToken;
import roomescape.configuration.annotation.docs.DocsAuthorizationExceptionResponse;
import roomescape.configuration.annotation.docs.DocsDeletableDataNotFoundExceptionResponse;
import roomescape.configuration.annotation.docs.DocsDuplicatedDateCreationResponse;
import roomescape.configuration.annotation.docs.DocsSuccessResponse;
import roomescape.domain.Role;
import roomescape.dto.business.AccessTokenContent;
import roomescape.dto.business.PaymentCreationContent;
import roomescape.dto.business.WaitingCreationContent;
import roomescape.dto.request.WaitingCreationRequest;
import roomescape.dto.response.WaitingResponse;
import roomescape.service.command.WaitingService;
import roomescape.service.query.WaitingQueryService;

@Tag(name = "WaitingController", description = "예약 대기 관련 API")
@RestController
@RequestMapping("/waiting")
public class WaitingController {

    private final WaitingService waitingService;
    private final WaitingQueryService waitingQueryService;

    public WaitingController(WaitingService waitingService, WaitingQueryService waitingQueryService) {
        this.waitingService = waitingService;
        this.waitingQueryService = waitingQueryService;
    }

    @Operation(summary = "Find All Waiting", description = "모든 예약 대기 조회")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @GetMapping
    @Authority(Role.ADMIN)
    public List<WaitingResponse> findAllWaiting() {
        return waitingQueryService.findAllWaiting();
    }

    @Operation(summary = "Add Waiting", description = "예약 대기 추가")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @DocsDuplicatedDateCreationResponse
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "결제 승인에 실패하는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "대기를 추가할 예약이 존재하지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "과거 날짜와 시간으로 대기를 생성하는 경우")
    })
    @PostMapping
    @Authority(Role.GENERAL)
    public ResponseEntity<WaitingResponse> addWaiting(
            @Valid @RequestBody WaitingCreationRequest request,
            @RequiredAccessToken AccessTokenContent token
    ) {
        WaitingCreationContent creationContent =
                new WaitingCreationContent(request.date(), request.themeId(), request.timeId(), token.id());
        PaymentCreationContent paymentCreationContent = new PaymentCreationContent(request);

        WaitingResponse waitingResponse = waitingService.addWaiting(creationContent, paymentCreationContent);
        return ResponseEntity.created(URI.create("/waiting/" + waitingResponse.id())).body(waitingResponse);
    }

    @Operation(summary = "Delete Waiting By Id", description = "예약 대기 삭제")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @DocsDeletableDataNotFoundExceptionResponse
    @DeleteMapping("/{id}")
    @Authority(Role.GENERAL)
    public ResponseEntity<Void> deleteWaitingById(
            @PathVariable("id") Long id
    ) {
        waitingService.deleteWaitingById(id);
        return ResponseEntity.noContent().build();
    }
}
