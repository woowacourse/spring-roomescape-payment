package roomescape.reservation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.domain.AuthInfo;
import roomescape.global.annotation.LoginUser;
import roomescape.reservation.controller.dto.ReservationPaymentRequest;
import roomescape.reservation.controller.dto.ReservationQueryRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.controller.dto.ReservationViewResponse;
import roomescape.reservation.controller.dto.ReservationWithStatus;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.WaitingReservationService;

@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final WaitingReservationService waitingReservationService;

    public ReservationController(ReservationService reservationService,
                                 WaitingReservationService waitingReservationService) {
        this.reservationService = reservationService;
        this.waitingReservationService = waitingReservationService;
    }

    @GetMapping("/reservations")
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
    public ResponseEntity<ReservationResponse> create(@LoginUser AuthInfo authInfo,
                                                      @RequestBody @Valid ReservationPaymentRequest reservationPaymentRequest) {
        ReservationResponse response = reservationService.createReservation(reservationPaymentRequest,
                authInfo.getId());

        return ResponseEntity.created(URI.create("/reservations/" + response.reservationId())).body(response);
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@LoginUser AuthInfo authInfo,
                       @PathVariable("id") @Min(1) long reservationId) {
        waitingReservationService.deleteReservation(authInfo, reservationId);
    }

    @GetMapping("/reservations/mine")
    public List<ReservationViewResponse> getMyReservations(@LoginUser AuthInfo authInfo) {
        List<ReservationWithStatus> reservationWithStatuses = reservationService.findReservations(authInfo);
        return waitingReservationService.convertReservationsWithStatusToViewResponses(reservationWithStatuses);
    }
}
