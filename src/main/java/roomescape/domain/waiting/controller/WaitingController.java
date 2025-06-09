package roomescape.domain.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import roomescape.domain.annotation.Authority;
import roomescape.domain.annotation.RequiredAccessToken;
import roomescape.domain.auth.dto.AccessTokenContent;
import roomescape.domain.member.domain.Role;
import roomescape.domain.payment.domain.Payment;
import roomescape.domain.payment.dto.PaymentCreationContent;
import roomescape.domain.payment.service.PaymentService;
import roomescape.domain.waiting.dto.WaitingCreationContent;
import roomescape.domain.waiting.request.WaitingCreationRequest;
import roomescape.domain.waiting.response.AddWaitingResponse;
import roomescape.domain.waiting.response.FindAllWaitingResponse;
import roomescape.domain.waiting.service.WaitingQueryService;
import roomescape.domain.waiting.service.WaitingService;

@Tag(name = "WaitingController", description = "예약 대기 관련 API")
@RestController
@RequestMapping("/waiting")
public class WaitingController {

    private final PaymentService paymentService;
    private final WaitingService waitingService;
    private final WaitingQueryService waitingQueryService;

    public WaitingController(
            PaymentService paymentService,
            WaitingService waitingService,
            WaitingQueryService waitingQueryService
    ) {
        this.paymentService = paymentService;
        this.waitingService = waitingService;
        this.waitingQueryService = waitingQueryService;
    }

    @Operation(summary = "Find All Waiting", description = "모든 예약 대기 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FindAllWaitingResponse.class)))),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    @GetMapping
    @Authority(Role.ADMIN)
    public List<FindAllWaitingResponse> findAllWaiting() {
        return waitingQueryService.findAllWaiting();
    }

    @Operation(summary = "Add Waiting", description = "예약 대기 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = AddWaitingResponse.class))),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "중복된 데이터를 추가하는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
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
        PaymentCreationContent paymentCreationContent = new PaymentCreationContent(request);
        Payment payment = paymentService.savePayment(paymentCreationContent);

        WaitingCreationContent creationContent =
                new WaitingCreationContent(request.date(), request.themeId(), request.timeId(), token.id());
        AddWaitingResponse response = waitingService.addWaiting(creationContent, payment.getId());
        return ResponseEntity.created(URI.create("/waiting/" + response.id())).body(response);
    }

    @Operation(summary = "Delete Waiting By Id", description = "예약 대기 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "삭제할 데이터가 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @Authority(Role.GENERAL)
    public ResponseEntity<Void> deleteWaitingById(
            @PathVariable("id") Long id
    ) {
        waitingService.deleteWaitingById(id);
        return ResponseEntity.noContent().build();
    }
}
