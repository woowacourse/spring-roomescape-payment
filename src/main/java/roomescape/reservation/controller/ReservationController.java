package roomescape.reservation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.domain.AuthInfo;
import roomescape.global.annotation.LoginUser;
import roomescape.global.restclient.PaymentWithRestClient;
import roomescape.reservation.controller.dto.*;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.WaitingReservationService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final WaitingReservationService waitingReservationService;
    private final PaymentWithRestClient paymentRestClient;

    public ReservationController(ReservationService reservationService,
                                 WaitingReservationService waitingReservationService, PaymentWithRestClient paymentRestClient) {
        this.reservationService = reservationService;
        this.waitingReservationService = waitingReservationService;
        this.paymentRestClient = paymentRestClient;
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

        PaymentRequest paymentRequest = new PaymentRequest(reservationPaymentRequest);
//        reservationService.validateAmount(reservationPaymentRequest); // TODO 예약 확정 전에 검증하는게 좋을듯
        PaymentResponse paymentResponse = paymentRestClient.confirm(paymentRequest);

        reservationService.reserve(reservationPaymentRequest, paymentResponse, authInfo.getId());

//        ReservationResponse response = reservationService.createReservation(reservationRequest, authInfo.getId());
//        return ResponseEntity.created(URI.create("/reservations/" + response.reservationId())).body(response);
        return ResponseEntity.created(URI.create("/reservations/" + "asdsad")).body(null);
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
