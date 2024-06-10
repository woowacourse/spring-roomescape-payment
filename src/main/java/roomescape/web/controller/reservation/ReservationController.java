package roomescape.web.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.login.LoginMember;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.UserReservationRequest;
import roomescape.dto.reservation.UserReservationResponse;
import roomescape.dto.reservation.UserReservationWaitingRequest;
import roomescape.service.reservation.ReservationCancelService;
import roomescape.service.reservation.ReservationRegisterService;
import roomescape.service.reservation.ReservationSearchService;

@Tag(name = "예약 관리")
@RestController
@RequestMapping("/reservations")
class ReservationController {

    private final ReservationRegisterService registerService;
    private final ReservationSearchService searchService;
    private final ReservationCancelService cancelService;

    public ReservationController(ReservationRegisterService registerService,
                                 ReservationSearchService searchService,
                                 ReservationCancelService cancelService
    ) {
        this.registerService = registerService;
        this.searchService = searchService;
        this.cancelService = cancelService;
    }

    @Operation(summary = "예약 등록", description = "사용자의 예약 정보를 받아 예약을 등록한다.")
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody UserReservationRequest userRequest,
                                                                 LoginMember loginMember
    ) {
        ReservationRequest reservationRequest = userRequest.toReservationRequest(loginMember.id());
        PaymentRequest paymentRequest = userRequest.toPaymentRequest();
        ReservationResponse response = registerService.registerReservation(reservationRequest, paymentRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @Operation(summary = "예약 대기 등록", description = "사용자의 예약 정보를 받아 예약 대기를 등록한다.")
    @PostMapping("/waiting")
    public ResponseEntity<ReservationResponse> createWaitingReservation(@RequestBody UserReservationWaitingRequest userRequest,
                                                                        LoginMember loginMember
    ) {
        ReservationRequest reservationRequest = userRequest.toReservationRequest(loginMember.id());
        ReservationResponse response = registerService.registerWaitingReservation(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @Operation(summary = "예약 결제 요청", description = "결제 대기 상태의 예약의 결제를 요청한다.")
    @PostMapping("/pay/{id}")
    public ResponseEntity<ReservationResponse> requestReservationPayment(@RequestBody PaymentRequest paymentRequest,
                                                                         @PathVariable Long id) {
        ReservationResponse response = registerService.requestPaymentByPaymentPending(id, paymentRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @Operation(summary = "예약 조회", description = "등록된 예약 중 특정 예약을 조회한다.")
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        ReservationResponse response = searchService.findReservation(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 예약 조회", description = "등록된 모든 예약을 조회한다.")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> responses = searchService.findAllReservedReservations();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "사용자 예약 조회", description = "로그인 사용자의 예약을 조회한다.")
    @GetMapping("/mine")
    public ResponseEntity<List<UserReservationResponse>> getReservationsByUser(LoginMember loginMember) {
        List<UserReservationResponse> responses = searchService.findReservationByMemberId(loginMember.id());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 삭제", description = "등록된 예약을 삭제한다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        cancelService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
