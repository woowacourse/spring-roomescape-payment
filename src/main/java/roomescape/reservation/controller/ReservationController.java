package roomescape.reservation.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import roomescape.payment.PaymentRequest;
import roomescape.payment.TossPaymentClient;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.ReservationSearchRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationsResponse;
import roomescape.reservation.dto.response.WaitingWithRanksResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.system.auth.annotation.Admin;
import roomescape.system.auth.annotation.MemberId;
import roomescape.system.dto.response.ApiResponse;

@RestController
public class ReservationController {
    private final ReservationService reservationService;
    private final TossPaymentClient tossPaymentClient;

    public ReservationController(ReservationService reservationService, TossPaymentClient tossPaymentClient) {
        this.reservationService = reservationService;
        this.tossPaymentClient = tossPaymentClient;
    }


    @Admin
    @GetMapping("/reservations")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationsResponse> getAllReservations() {
        return ApiResponse.success(reservationService.findAllReservations());
    }

    @GetMapping("/reservations-mine")
    public ApiResponse<WaitingWithRanksResponse> getMemberReservations(@MemberId final Long memberId) {
        return ApiResponse.success(reservationService.findWaitingWithRankById(memberId));
    }

    @Admin
    @GetMapping("/reservations/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationsResponse> getReservationBySearching(
            ReservationSearchRequest request
    ) {
        return ApiResponse.success(
                reservationService.findFilteredReservations(request)
        );
    }

    @Admin
    @DeleteMapping("/reservations/{id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeReservation(
            @MemberId final Long memberId,
            @NotNull(message = "reservationId는 null일 수 없습니다.") @PathVariable("id") final Long reservationId,
            @NotNull(message = "status는 null일 수 없습니다.") @RequestParam("status") final String status
    ) {
        reservationService.updateState(memberId, reservationId, status);

        return ApiResponse.success();
    }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReservationResponse> saveReservation(
            @Valid @RequestBody final ReservationRequest reservationRequest,
            @MemberId final Long memberId,
            final HttpServletResponse response
    ) {
        tossPaymentClient.confirmPayment(
                new PaymentRequest(reservationRequest.paymentKey(), reservationRequest.orderId(),
                        reservationRequest.amount(), reservationRequest.paymentType()));
        ReservationResponse reservationResponse = reservationService.addReservation(reservationRequest, memberId);
        return getResponse(reservationResponse, response);
    }

    @PostMapping("/reservations/waiting")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReservationResponse> saveWaiting(
            @Valid @RequestBody final ReservationRequest reservationRequest,
            @MemberId final Long memberId,
            final HttpServletResponse response
    ) {
        ReservationResponse reservationResponse = reservationService.addWaiting(reservationRequest, memberId);
        return getResponse(reservationResponse, response);
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeReservation(
            @MemberId final Long memberId,
            @NotNull(message = "reservationId는 null 또는 공백일 수 없습니다.") @PathVariable("id") final Long reservationId
    ) {
        reservationService.removeReservationById(reservationId, memberId);

        return ApiResponse.success();
    }

    private ApiResponse<ReservationResponse> getResponse(
            ReservationResponse reservationResponse,
            HttpServletResponse response
    ) {
        response.setHeader(HttpHeaders.LOCATION, "/reservations/" + reservationResponse.id());
        return ApiResponse.success(reservationResponse);
    }
}
