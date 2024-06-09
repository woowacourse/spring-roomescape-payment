package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.domain.AuthInfo;
import roomescape.global.annotation.LoginUser;
import roomescape.reservation.controller.dto.*;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.WaitingReservationService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "Reservation API", description = "예약 관련 API 입니다.")
public class ReservationController {

    private final ReservationService reservationService;
    private final WaitingReservationService waitingReservationService;

    public ReservationController(ReservationService reservationService,
                                 WaitingReservationService waitingReservationService) {
        this.reservationService = reservationService;
        this.waitingReservationService = waitingReservationService;
    }

    @GetMapping("/reservations")
    @Operation(summary = "예약을 조회한다.",
            description = "조회 하고자 하는 조건을 인자로 받는다." +
                    "인자가 없는 경우 해당 조건은 무시한다. " +
                    "조회가 정상적으로 처리된 경우이며, 조회된 예약 정보들을 리스트로 반환한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "500", description = "잘못된 입력 또는 서버 오류")
    })
    @Parameters({
            @Parameter(name = "themeId", description = "조회하고자 하는 테마의 id"),
            @Parameter(name = "memberId", description = "조회하고자 하는 멤버의 id"),
            @Parameter(name = "dateFrom", description = "조회 시작 날짜이며 yyyy-mm-dd 형식이다. 기본값은 일주일 전이다."),
            @Parameter(name = "dateTo", description = "조회 마지막 날짜이며 yyyy-mm-dd 형식이다. 기본값은 오늘이다.")
    })
    public List<ReservationResponse> reservations(
            @RequestParam(value = "themeId", required = false) Long themeId,
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "dateFrom", required = false) LocalDate startDate,
            @RequestParam(value = "dateTo", required = false) LocalDate endDate
    ) {
        return reservationService.findReservations(
                new ReservationQueryRequest(themeId, memberId, startDate, endDate));
    }

    @PostMapping("/reservations")
    @Operation(summary = "예약을 생성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "이미 예약이 되어있는 상품이거나 상품 금액과 결제 금액이 일치하지 않는 경우 발생")
    })
    @Parameters({
            @Parameter(name = "authInfo", description = "로그인한 유저의 정보", required = true),
            @Parameter(name = "reservationPaymentRequest", description = "예약 및 결제 정보 DTO", required = true)
    })
    public ResponseEntity<ReservationResponse> create(@LoginUser AuthInfo authInfo,
                                                      @RequestBody @Valid ReservationPaymentRequest reservationPaymentRequest) {
        ReservationResponse response = reservationService.reserve(reservationPaymentRequest, authInfo.getId());

        return ResponseEntity.created(URI.create("/reservations/" + response.reservationId())).body(response);
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "예약을 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "400", description = "로그인을 하지 않았거나 일치하는 예약 정보가 없는 경우 발생"),
            @ApiResponse(responseCode = "403", description = "다른 사용자의 예약을 삭제하려는 경우 발생")
    })
    @Parameters({
            @Parameter(name = "authInfo", description = "로그인한 유저의 정보", required = true),
            @Parameter(name = "id", description = "예약 ID", required = true)
    })
    public void delete(@LoginUser AuthInfo authInfo,
                                       @PathVariable("id") @Min(1) long reservationId) {
        waitingReservationService.deleteReservation(authInfo, reservationId);
    }

    @GetMapping("/reservations/mine")
    @Operation(summary = "내 예약을 조회한다.", description = "내 예약 목록을 리스트로 받는다.")
    @ApiResponse(responseCode = "200", description = "OK")
    @Parameter(name = "authInfo", description = "로그인한 유저의 정보", required = true)
    public List<ReservationViewResponse> getMyReservations(@LoginUser AuthInfo authInfo) {
        List<ReservationWithStatus> reservationWithStatuses = reservationService.findReservations(authInfo);
        return waitingReservationService.convertReservationsWithStatusToViewResponses(reservationWithStatuses);
    }
}
