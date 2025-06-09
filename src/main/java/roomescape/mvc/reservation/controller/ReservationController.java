package roomescape.mvc.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import roomescape.annotation.Authority;
import roomescape.annotation.RequiredAccessToken;
import roomescape.mvc.auth.dto.AccessTokenContent;
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.payment.domain.Payment;
import roomescape.mvc.payment.dto.PaymentCreationContent;
import roomescape.mvc.payment.service.PaymentService;
import roomescape.mvc.reservation.dto.ReservationCreationContent;
import roomescape.mvc.reservation.request.AdminReservationRequest;
import roomescape.mvc.reservation.request.ReservationCreationRequest;
import roomescape.mvc.reservation.response.AddReservationByAdmin;
import roomescape.mvc.reservation.response.AddReservationByMember;
import roomescape.mvc.reservation.response.FindAllReservationResponse;
import roomescape.mvc.reservation.response.FindReservationsByFilter;
import roomescape.mvc.reservation.response.ReservationStatusResponse;
import roomescape.mvc.reservation.service.ReservationQueryService;
import roomescape.mvc.reservation.service.ReservationService;

@Tag(name = "ReservationController", description = "예약 관련 API")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationQueryService reservationQueryService;
    private final PaymentService paymentService;

    public ReservationController(
            ReservationService reservationService,
            ReservationQueryService reservationQueryService,
            PaymentService paymentService
    ) {
        this.reservationService = reservationService;
        this.reservationQueryService = reservationQueryService;
        this.paymentService = paymentService;
    }

    @Operation(summary = "Find All Reservation", description = "모든 예약 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FindAllReservationResponse.class)))),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    @GetMapping
    @Authority(Role.ADMIN)
    public List<FindAllReservationResponse> findAllReservations() {
        return reservationQueryService.findAllReservations();
    }

    @Operation(summary = "Find All Reservation By Filter", description = "필터를 통해 예약 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FindReservationsByFilter.class)))),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    @GetMapping(params = {"memberId", "themeId", "from", "to"})
    @Authority(Role.ADMIN)
    public List<FindReservationsByFilter> findReservationsByFilter(
            @RequestParam("memberId") Long memberId,
            @RequestParam("themeId") Long themeId,
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to
    ) {
        return reservationQueryService.findReservationsByFilter(memberId, themeId, from, to);
    }

    @Operation(summary = "Find Member's All Reservation States", description = "회원의 예약 상태를 모두 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = ReservationStatusResponse.class))),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = AddReservationByAdmin.class))),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "중복된 데이터를 추가하는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "과거 에약을 생성하는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @Authority(Role.ADMIN)
    public ResponseEntity<AddReservationByAdmin> addReservationByAdmin(
            @Valid @RequestBody AdminReservationRequest request
    ) {
        ReservationCreationContent creationRequest = new ReservationCreationContent(request);
        AddReservationByAdmin response = reservationService.addReservationWithoutPayment(request.memberId(),
                creationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/reservation/" + response.id()))
                .body(response);
    }

    @Operation(summary = "Add Reservation By Member", description = "회원의 예약 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = AddReservationByMember.class))),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "중복된 데이터를 추가하는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "과거 예약을 생성하는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "결제 승인에 실패하는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/mine")
    @Authority(Role.GENERAL)
    public ResponseEntity<AddReservationByMember> addReservationByMember(
            @Valid @RequestBody ReservationCreationRequest request,
            @RequiredAccessToken AccessTokenContent accessTokenContent
    ) {
        PaymentCreationContent paymentCreationContent = new PaymentCreationContent(request);
        Payment payment = paymentService.savePayment(paymentCreationContent);

        ReservationCreationContent creationContent = new ReservationCreationContent(request);
        AddReservationByMember response = reservationService.addReservationWithPayment(
                accessTokenContent.id(), payment.getId(), creationContent);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/reservation/" + response.id()))
                .body(response);
    }

    @Operation(summary = "DELETE RESERVATION BY ID", description = "ID를 기준으로 예약 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "삭제할 데이터가 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{reservationId}")
    @Authority(Role.ADMIN)
    public ResponseEntity<Void> deleteReservationById(
            @PathVariable("reservationId") Long id
    ) {
        reservationService.deleteReservationById(id);
        return ResponseEntity.noContent().build();
    }
}
