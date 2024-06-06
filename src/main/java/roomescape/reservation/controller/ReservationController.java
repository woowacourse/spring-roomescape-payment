package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameters;
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
            description = "조회 하고자 하는 조건을 인자로 받는다. 인자가 없는 경우 해당 조건은 무시한다.")
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
    public ResponseEntity<ReservationResponse> create(@LoginUser AuthInfo authInfo,
                                                      @RequestBody @Valid ReservationPaymentRequest reservationPaymentRequest) {
        ReservationResponse response = reservationService.reserve(reservationPaymentRequest, authInfo.getId());

        return ResponseEntity.created(URI.create("/reservations/" + response.reservationId())).body(response);
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "예약을 삭제한다.")
    public void delete(@LoginUser AuthInfo authInfo,
                                       @PathVariable("id") @Min(1) long reservationId) {
        waitingReservationService.deleteReservation(authInfo, reservationId);
    }

    @GetMapping("/reservations/mine")
    @Operation(summary = "내 예약을 조회한다.")
    public List<ReservationViewResponse> getMyReservations(@LoginUser AuthInfo authInfo) {
        List<ReservationWithStatus> reservationWithStatuses = reservationService.findReservations(authInfo);
        return waitingReservationService.convertReservationsWithStatusToViewResponses(reservationWithStatuses);
    }
}
