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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.response.PaymentCancelResponse;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.ReservationSearchRequest;
import roomescape.reservation.dto.response.MyReservationsResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationsResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.ReservationWithPaymentService;
import roomescape.system.auth.annotation.Admin;
import roomescape.system.auth.annotation.MemberId;
import roomescape.system.dto.response.ApiResponse;
import roomescape.system.exception.RoomEscapeException;

@RestController
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
    public ApiResponse<ReservationsResponse> getAllReservations() {
        return ApiResponse.success(reservationService.findAllReservations());
    }

    @GetMapping("/reservations-mine")
    public ApiResponse<MyReservationsResponse> getMemberReservations(@MemberId Long memberId) {
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
    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeReservation(
            @MemberId Long memberId,
            @NotNull(message = "reservationId는 null일 수 없습니다.") @PathVariable("id") Long reservationId
    ) {
        PaymentCancelRequest paymentCancelRequest = reservationWithPaymentService.removeReservationWithPayment(
                reservationId, memberId);
        PaymentCancelResponse paymentCancelResponse = paymentClient.cancelPayment(paymentCancelRequest);
        reservationWithPaymentService.updateCanceledTime(paymentCancelRequest.paymentKey(),
                paymentCancelResponse.canceledAt());
        return ApiResponse.success();
    }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReservationResponse> saveReservation(
            @Valid @RequestBody ReservationRequest reservationRequest,
            @MemberId Long memberId,
            HttpServletResponse response
    ) {
        PaymentRequest paymentRequest = reservationRequest.getPaymentRequest();
        PaymentResponse paymentResponse = paymentClient.confirmPayment(paymentRequest);

        try {
            ReservationResponse reservationResponse = reservationWithPaymentService.addReservationWithPayment(
                    reservationRequest,
                    paymentResponse, memberId);
            return getCreatedReservationResponse(reservationResponse, response);
        } catch (RoomEscapeException e) {
            PaymentCancelRequest cancelRequest = new PaymentCancelRequest(paymentRequest.paymentKey(),
                    paymentRequest.amount(), e.getMessage());
            PaymentCancelResponse paymentCancelResponse = paymentClient.cancelPayment(cancelRequest);
            reservationWithPaymentService.cancelPaymentWhenErrorOccurred(paymentCancelResponse,
                    paymentRequest.paymentKey());
            throw e;
        }
    }

    @PostMapping("/reservations/waiting")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReservationResponse> saveWaiting(
            @Valid @RequestBody ReservationRequest reservationRequest,
            @MemberId Long memberId,
            HttpServletResponse response
    ) {
        ReservationResponse reservationResponse = reservationService.addWaiting(reservationRequest, memberId);
        return getCreatedReservationResponse(reservationResponse, response);
    }

    @DeleteMapping("/reservations/waiting/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<ReservationResponse> saveWaiting(
            @MemberId Long memberId,
            @NotNull(message = "reservationId는 null 또는 공백일 수 없습니다.") @PathVariable("id") Long reservationId
    ) {
        reservationService.cancelWaiting(reservationId, memberId);
        return ApiResponse.success();
    }

    @Admin
    @PostMapping("/reservations/waiting/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> approveWaiting(
            @MemberId Long memberId,
            @NotNull(message = "reservationId는 null 또는 공백일 수 없습니다.") @PathVariable("id") Long reservationId
    ) {
        reservationService.approveWaiting(reservationId, memberId);

        return ApiResponse.success();
    }

    @Admin
    @PostMapping("/reservations/waiting/{id}/deny")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> denyWaiting(
            @MemberId Long memberId,
            @NotNull(message = "reservationId는 null 또는 공백일 수 없습니다.") @PathVariable("id") Long reservationId
    ) {
        reservationService.denyWaiting(reservationId, memberId);

        return ApiResponse.success();
    }

    private ApiResponse<ReservationResponse> getCreatedReservationResponse(
            ReservationResponse reservationResponse,
            HttpServletResponse response
    ) {
        response.setHeader(HttpHeaders.LOCATION, "/reservations/" + reservationResponse.id());
        return ApiResponse.success(reservationResponse);
    }
}
