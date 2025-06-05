package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.configuration.annotation.Authority;
import roomescape.configuration.annotation.RequiredAccessToken;
import roomescape.configuration.annotation.docs.DocsAuthorizationExceptionResponse;
import roomescape.configuration.annotation.docs.DocsDeletableDataNotFoundExceptionResponse;
import roomescape.configuration.annotation.docs.DocsDuplicatedDateCreationResponse;
import roomescape.configuration.annotation.docs.DocsSuccessResponse;
import roomescape.domain.Role;
import roomescape.dto.business.AccessTokenContent;
import roomescape.dto.business.PaymentHistoryCreationContent;
import roomescape.dto.business.ReservationCreationContent;
import roomescape.dto.request.AdminReservationRequest;
import roomescape.dto.request.ReservationCreationRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationStatusResponse;
import roomescape.service.command.ReservationService;
import roomescape.service.query.ReservationQueryService;

@Tag(name = "ReservationController", description = "예약 관련 API")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationQueryService reservationQueryService;

    public ReservationController(
            ReservationService reservationService,
            ReservationQueryService reservationQueryService
    ) {
        this.reservationService = reservationService;
        this.reservationQueryService = reservationQueryService;
    }

    @Operation(summary = "Find All Reservation", description = "모든 예약 조회")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @GetMapping
    @Authority(Role.ADMIN)
    public List<ReservationResponse> findAllReservations() {
        return reservationQueryService.findAllReservations();
    }

    @Operation(summary = "Find All Reservation By Filter", description = "필터를 통해 예약 조회")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @GetMapping(params = {"memberId", "themeId", "from", "to"})
    @Authority(Role.ADMIN)
    public List<ReservationResponse> searchReservationsByFilter(
            @RequestParam("memberId") Long memberId,
            @RequestParam("themeId") Long themeId,
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to
    ) {
        return reservationQueryService.findReservationsByFilter(memberId, themeId, from, to);
    }

    @Operation(summary = "Find Member's ALL Reservation States", description = "회원의 예약 상태를 모두 조회")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "과거 에약을 생성하는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "결제 승인에 실패하는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/state")
    @Authority(Role.GENERAL)
    public ReservationStatusResponse findAllReservationStateByMember(
            @RequiredAccessToken AccessTokenContent accessTokenContent
    ) {
        return reservationQueryService.findAllReservationStatusByMember(accessTokenContent.id());
    }

    @Operation(summary = "Add Reservation By Admin", description = "관리자의 예약 추가")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @DocsDuplicatedDateCreationResponse
    @ApiResponse(responseCode = "400", description = "과거 에약을 생성하는 경우",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping
    @Authority(Role.ADMIN)
    public ResponseEntity<ReservationResponse> addReservationByAdmin(
            @Valid @RequestBody AdminReservationRequest request
    ) {
        ReservationCreationContent creationRequest = new ReservationCreationContent(request);
        ReservationResponse reservationResponse =
                reservationService.addReservation(request.memberId(), creationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/reservation/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "Add Reservation By Member", description = "회원의 예약 추가")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @DocsDuplicatedDateCreationResponse
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "과거 예약을 생성하는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "결제 승인에 실패하는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/mine")
    @Authority(Role.GENERAL)
    public ResponseEntity<ReservationResponse> addReservationByMember(
            @Valid @RequestBody ReservationCreationRequest request,
            @RequiredAccessToken AccessTokenContent accessTokenContent
    ) {
        ReservationCreationContent creationContent = new ReservationCreationContent(request);
        PaymentHistoryCreationContent paymentHistoryCreationContent = new PaymentHistoryCreationContent(request);

        ReservationResponse reservationResponse = reservationService.addReservation(accessTokenContent.id(),
                creationContent, paymentHistoryCreationContent);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/reservation/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "DELETE RESERVATION BY ID", description = "ID를 기준으로 예약 삭제")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @DocsDeletableDataNotFoundExceptionResponse
    @DeleteMapping("/{reservationId}")
    @Authority(Role.ADMIN)
    public ResponseEntity<Void> deleteReservationById(
            @PathVariable("reservationId") Long id
    ) {
        reservationService.deleteReservationById(id);
        return ResponseEntity.noContent().build();
    }
}
