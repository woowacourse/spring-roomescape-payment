package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.PaymentCancelResponse;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.reservation.dto.request.AdminReservationRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.WaitingRequest;
import roomescape.reservation.dto.response.MyReservationsResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationsResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.ReservationWithPaymentService;
import roomescape.system.auth.annotation.Admin;
import roomescape.system.auth.annotation.LoginRequired;
import roomescape.system.auth.annotation.MemberId;
import roomescape.system.dto.response.ErrorResponse;
import roomescape.system.dto.response.RoomEscapeApiResponse;
import roomescape.system.exception.RoomEscapeException;

@RestController
@Tag(name = "3. 예약 API", description = "예약 및 대기 정보를 추가 / 조회 / 삭제할 때 사용합니다.")
public class ReservationController {

    private final ReservationWithPaymentService reservationWithPaymentService;
    private final ReservationService reservationService;
    private final PaymentClient paymentClient;

    public ReservationController(ReservationWithPaymentService reservationWithPaymentService,
                                 ReservationService reservationService, PaymentClient paymentClient) {
        this.reservationWithPaymentService = reservationWithPaymentService;
        this.reservationService = reservationService;
        this.paymentClient = paymentClient;
    }


    @Admin
    @GetMapping("/reservations")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "모든 예약 정보 조회", description = "관리자가 모든 예약 정보를 조회합니다. 대기중인 예약은 조회하지 않습니다.", tags = "관리자 로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    })
    public RoomEscapeApiResponse<ReservationsResponse> getAllReservations() {
        return RoomEscapeApiResponse.success(reservationService.findAllReservations());
    }

    @LoginRequired
    @GetMapping("/reservations-mine")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "회원의 예약 및 대기 조회", description = "현재 로그인된 회원의 모든 예약 및 대기 정보를 조회합니다.", tags = "로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    })
    public RoomEscapeApiResponse<MyReservationsResponse> getMemberReservations(
            @MemberId @Parameter(hidden = true) Long memberId) {
        return RoomEscapeApiResponse.success(reservationService.findMemberReservations(memberId));
    }

    @Admin
    @GetMapping("/reservations/search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "관리자의 예약 검색", description = "관리자가 테마, 회원, 날짜 범위로 예약을 조회합니다. 검색 조건이 없으면 모든 예약을 조회합니다.", tags = "관리자 로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "날짜 범위를 지정할 때, 종료 날짜는 시작 날짜 이전일 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RoomEscapeApiResponse<ReservationsResponse> getReservationBySearching(
            @RequestParam(required = false) @Parameter(description = "테마 ID") Long themeId,
            @RequestParam(required = false) @Parameter(description = "회원 ID") Long memberId,
            @RequestParam(required = false) @Parameter(description = "yyyy-MM-dd 형식으로 입력해주세요", example = "2024-06-10") LocalDate dateFrom,
            @RequestParam(required = false) @Parameter(description = "yyyy-MM-dd 형식으로 입력해주세요", example = "2024-06-10") LocalDate dateTo
    ) {
        return RoomEscapeApiResponse.success(
                reservationService.findFilteredReservations(themeId, memberId, dateFrom, dateTo));
    }

    @Admin
    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "관리자의 예약 취소", description = "결제 대기중인 예약은 바로 삭제하고, 결제 완료 된 예약에 대해선 결제 취소를 요청합니다.",
            tags = "관리자 로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공"),
            @ApiResponse(responseCode = "404", description = "예약 또는 결제 정보를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RoomEscapeApiResponse<Void> removeReservation(
            @MemberId @Parameter(hidden = true) Long memberId,
            @NotNull(message = "reservationId는 null일 수 없습니다.") @PathVariable("id") @Parameter(description = "예약 ID") Long reservationId
    ) {

        if (reservationWithPaymentService.isNotPaidReservation(reservationId)) {
            reservationService.removeReservationById(reservationId, memberId);
            return RoomEscapeApiResponse.success();
        }

        PaymentCancelRequest paymentCancelRequest = reservationWithPaymentService.removeReservationWithPayment(
                reservationId, memberId);

        PaymentCancelResponse paymentCancelResponse = paymentClient.cancelPayment(paymentCancelRequest);

        reservationWithPaymentService.updateCanceledTime(paymentCancelRequest.paymentKey(),
                paymentCancelResponse.canceledAt());

        return RoomEscapeApiResponse.success();
    }

    @LoginRequired
    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "예약 추가", description = "결제 위젯에서 받은 결제 정보를 확인하고, 결제가 승인되면 예약을 추가합니다.", tags = "로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true,
                    headers = @Header(name = HttpHeaders.LOCATION, description = "생성된 예약 정보 URL", schema = @Schema(example = "/reservations/1")))
    })
    public RoomEscapeApiResponse<ReservationResponse> saveReservation(
            @Valid @RequestBody ReservationRequest reservationRequest,
            @MemberId @Parameter(hidden = true) Long memberId,
            HttpServletResponse response
    ) {
        PaymentRequest paymentRequest = reservationRequest.getPaymentRequest();
        PaymentResponse paymentResponse = paymentClient.confirmPayment(paymentRequest);

        try {
            ReservationResponse reservationResponse = reservationWithPaymentService.addReservationWithPayment(
                    reservationRequest, paymentResponse, memberId);
            return getCreatedReservationResponse(reservationResponse, response);
        } catch (RoomEscapeException e) {
            PaymentCancelRequest cancelRequest = new PaymentCancelRequest(paymentRequest.paymentKey(),
                    paymentRequest.amount(), e.getMessage());

            PaymentCancelResponse paymentCancelResponse = paymentClient.cancelPayment(cancelRequest);

            reservationWithPaymentService.saveCanceledPayment(paymentCancelResponse, paymentResponse.approvedAt(),
                    paymentRequest.paymentKey());
            throw e;
        }
    }

    @Admin
    @PostMapping("/reservations/admin")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "관리자 예약 추가", description = "관리자가 직접 예약을 추가합니다. 추가된 예약은 결제 대기 상태로 지정됩니다.", tags = "관리자 로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true,
                    headers = @Header(name = HttpHeaders.LOCATION, description = "생성된 예약 정보 URL", schema = @Schema(example = "/reservations/1"))),
            @ApiResponse(responseCode = "409", description = "예약이 이미 존재합니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RoomEscapeApiResponse<ReservationResponse> saveReservationByAdmin(
            @Valid @RequestBody AdminReservationRequest adminReservationRequest,
            HttpServletResponse response
    ) {
        ReservationResponse reservationResponse = reservationService.addReservationByAdmin(adminReservationRequest);
        return getCreatedReservationResponse(reservationResponse, response);
    }

    @Admin
    @GetMapping("/reservations/waiting")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "모든 예약 대기 조회", description = "관리자가 모든 예약 대기 정보를 조회합니다.", tags = "관리자 로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    })
    public RoomEscapeApiResponse<ReservationsResponse> getAllWaiting() {
        return RoomEscapeApiResponse.success(reservationService.findAllWaiting());
    }

    @LoginRequired
    @PostMapping("/reservations/waiting")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "예약 대기 신청", description = "이미 예약이 되어있는 날짜, 테마에 예약 대기를 신청합니다.", tags = "로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true,
                    headers = @Header(name = HttpHeaders.LOCATION, description = "생성된 예약 정보 URL", schema = @Schema(example = "/reservations/1")))
    })
    public RoomEscapeApiResponse<ReservationResponse> saveWaiting(
            @Valid @RequestBody WaitingRequest waitingRequest,
            @MemberId @Parameter(hidden = true) Long memberId,
            HttpServletResponse response
    ) {
        ReservationResponse reservationResponse = reservationService.addWaiting(waitingRequest, memberId);
        return getCreatedReservationResponse(reservationResponse, response);
    }

    @LoginRequired
    @DeleteMapping("/reservations/waiting/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "회원의 예약 대기 취소", description = "회원이 자신이 한 예약 대기를 취소합니다.", tags = "로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공"),
            @ApiResponse(responseCode = "404", description = "회원의 예약 대기 정보를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RoomEscapeApiResponse<Void> deleteWaiting(
            @MemberId @Parameter(hidden = true) Long memberId,
            @NotNull(message = "reservationId는 null 또는 공백일 수 없습니다.") @PathVariable("id") @Parameter(description = "예약 ID") Long reservationId
    ) {
        reservationService.cancelWaiting(reservationId, memberId);
        return RoomEscapeApiResponse.success();
    }

    @Admin
    @PostMapping("/reservations/waiting/{id}/approve")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "대기 중인 예약 승인", description = "관리자가 기존의 예약이 취소되었을 때 대기 중인 예약을 '결제 대기' 상태로 승인합니다.", tags = "관리자 로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "예약 대기 정보를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "확정된 예약이 존재하여 대기 중인 예약을 승인할 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RoomEscapeApiResponse<Void> approveWaiting(
            @MemberId @Parameter(hidden = true) Long memberId,
            @NotNull(message = "reservationId는 null 또는 공백일 수 없습니다.") @PathVariable("id") @Parameter(description = "예약 ID") Long reservationId
    ) {
        reservationService.approveWaiting(reservationId, memberId);

        return RoomEscapeApiResponse.success();
    }

    @Admin
    @PostMapping("/reservations/waiting/{id}/deny")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "대기 중인 예약 거절", description = "관리자가 대기 중인 예약을 거절합니다.", tags = "관리자 로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "대기 중인 예약 거절 성공"),
            @ApiResponse(responseCode = "404", description = "예약 대기 정보를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RoomEscapeApiResponse<Void> denyWaiting(
            @MemberId @Parameter(hidden = true) Long memberId,
            @NotNull(message = "reservationId는 null 또는 공백일 수 없습니다.") @PathVariable("id") @Parameter(description = "예약 ID") Long reservationId
    ) {
        reservationService.denyWaiting(reservationId, memberId);

        return RoomEscapeApiResponse.success();
    }

    private RoomEscapeApiResponse<ReservationResponse> getCreatedReservationResponse(
            ReservationResponse reservationResponse,
            HttpServletResponse response
    ) {
        response.setHeader(HttpHeaders.LOCATION, "/reservations/" + reservationResponse.id());
        return RoomEscapeApiResponse.success(reservationResponse);
    }
}
