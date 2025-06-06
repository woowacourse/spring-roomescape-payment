package roomescape.mvc.waiting.controller;

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
import roomescape.annotation.Authority;
import roomescape.annotation.RequiredAccessToken;
import roomescape.annotation.docs.DocsAuthorizationExceptionResponse;
import roomescape.annotation.docs.DocsDeletableDataNotFoundExceptionResponse;
import roomescape.annotation.docs.DocsDuplicatedDateCreationResponse;
import roomescape.annotation.docs.DocsSuccessResponse;
import roomescape.mvc.auth.dto.AccessTokenContent;
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.payment.dto.PaymentCreationContent;
import roomescape.mvc.waiting.dto.WaitingCreationContent;
import roomescape.mvc.waiting.request.WaitingCreationRequest;
import roomescape.mvc.waiting.response.AddWaitingResponse;
import roomescape.mvc.waiting.response.FindAllWaitingResponse;
import roomescape.mvc.waiting.service.WaitingQueryService;
import roomescape.mvc.waiting.service.WaitingService;

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
    public List<FindAllWaitingResponse> findAllWaiting() {
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
    public ResponseEntity<AddWaitingResponse> addWaiting(
            @Valid @RequestBody WaitingCreationRequest request,
            @RequiredAccessToken AccessTokenContent token
    ) {
        WaitingCreationContent creationContent =
                new WaitingCreationContent(request.date(), request.themeId(), request.timeId(), token.id());
        PaymentCreationContent paymentCreationContent = new PaymentCreationContent(request);

        AddWaitingResponse response = waitingService.addWaiting(creationContent, paymentCreationContent);
        return ResponseEntity.created(URI.create("/waiting/" + response.id())).body(response);
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
