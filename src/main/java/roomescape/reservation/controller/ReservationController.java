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
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.PaymentCancelResponse;
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


    @GetMapping("/reservations")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationsResponse> getAllReservations() {
        return ApiResponse.success(reservationService.findAllReservations());
    }

    @GetMapping("/reservations-mine")
    public ApiResponse<MyReservationsResponse> getMemberReservations(@MemberId Long memberId) {
        return ApiResponse.success(reservationService.findMemberReservations(memberId));
    }

    @Admin
    @GetMapping("/reservations/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationsResponse> getReservationBySearching(ReservationSearchRequest request) {
        return ApiResponse.success(reservationService.findFilteredReservations(request));
    }

    @Admin
    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeReservation(
            @MemberId Long memberId,
            @NotNull(message = "reservationId는 null일 수 없습니다.") @PathVariable("id") Long reservationId
    ) {

        // 예약 대기 상태에서 관리자가 승인한 경우. 결제가 되지 않았기에 바로 제거합니다.
        if (reservationWithPaymentService.isNotPaidReservation(reservationId)) {
            reservationService.removeReservationById(reservationId, memberId);
            return ApiResponse.success();
        }

        // DB에서 예약 정보와 결제 정보를 지우고, 결제 취소 테이블에 기존 결제 정보를 옮깁니다.
        // 이때, 결제 취소 테이블에 있는 취소 시간은 현재 시간으로 입력합니다.
        PaymentCancelRequest paymentCancelRequest = reservationWithPaymentService.removeReservationWithPayment(
                reservationId, memberId);

        // DB 처리에서 문제가 없으면, API에 결제 취소를 요청합니다.
        PaymentCancelResponse paymentCancelResponse = paymentClient.cancelPayment(paymentCancelRequest);

        // 결제 취소 테이블에 있는 취소 시간을, API 로부터 받은 실제 취소 시간으로 업데이트 합니다.
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
                    reservationRequest, paymentResponse, memberId);
            return getCreatedReservationResponse(reservationResponse, response);
        } catch (RoomEscapeException e) {
            PaymentCancelRequest cancelRequest = new PaymentCancelRequest(paymentRequest.paymentKey(),
                    paymentRequest.amount(), e.getMessage());

            // 결제 API 에 취소를 요청합니다.
            PaymentCancelResponse paymentCancelResponse = paymentClient.cancelPayment(cancelRequest);

            // API에서 받은 취소 정보와, 기존 결제 정보를 결제 취소 테이블에 입력합니다.
            reservationWithPaymentService.saveCanceledPayment(paymentCancelResponse, paymentResponse.approvedAt(),
                    paymentRequest.paymentKey());
            throw e;
        }
    }

    @GetMapping("/reservations/waiting")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationsResponse> getAllWaiting() {
        return ApiResponse.success(reservationService.findAllWaiting());
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
    public ApiResponse<ReservationResponse> deleteWaiting(
            @MemberId Long memberId,
            @NotNull(message = "reservationId는 null 또는 공백일 수 없습니다.") @PathVariable("id") Long reservationId
    ) {
        reservationService.cancelWaiting(reservationId, memberId);
        return ApiResponse.success();
    }

    @Admin
    @PostMapping("/reservations/waiting/{id}/approve")
    @ResponseStatus(HttpStatus.OK)
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
