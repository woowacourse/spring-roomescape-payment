package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.LoginMemberConverter;
import roomescape.domain.reservation.Status;
import roomescape.dto.LoginMember;
import roomescape.dto.request.reservation.ReservationInformRequest;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.request.reservation.WaitingRequest;
import roomescape.dto.response.reservation.MyReservationWebResponse;
import roomescape.dto.response.reservation.ReservationInformResponse;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.service.ReservationService;

@Tag(name = "사용자 예약 API", description = "사용자 예약 관련 API 입니다.")
@RestController
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "사용자 예약 조회 API")
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> findAllByReservation() {
        List<ReservationResponse> responses = reservationService.findAllByStatus(Status.RESERVATION);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "사용자 예약 추가 API")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByClient(
            @LoginMemberConverter LoginMember loginMember,
            @RequestBody @Valid ReservationRequest reservationRequest) {
        ReservationResponse response = reservationService.saveReservationWithPaymentByClient(loginMember, reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @Operation(summary = "사용자 예약 대기 추가 API")
    @PostMapping("/waitings")
    public ResponseEntity<ReservationResponse> saveWaitingByClient(
            @LoginMemberConverter LoginMember loginMember,
            @RequestBody @Valid WaitingRequest waitingRequest
    ) {
        ReservationResponse response = reservationService.saveWaitingByClient(loginMember, waitingRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @Operation(summary = "사용자 예약 및 예약 대기 삭제 API")
    @DeleteMapping(value = {"/reservations/{id}", "/waitings/{id}"})
    public ResponseEntity<Void> deleteByReservation(@PathVariable long id, @LoginMemberConverter LoginMember loginMember) {
        reservationService.deleteById(id, loginMember);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 내 예약 조회 API")
    @GetMapping("/reservations/mine")
    public ResponseEntity<List<MyReservationWebResponse>> findMyReservations(
            @LoginMemberConverter LoginMember loginMember) {
        List<MyReservationWebResponse> responses = reservationService.findMyReservations(loginMember.id());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "사용자 결제 승인 API")
    @PostMapping("/reservation/approve")
    public ResponseEntity<ReservationResponse> approvePaymentWaiting(
            @RequestBody @Valid ReservationInformRequest reservationRequest
    ) {
        return ResponseEntity.created(URI.create("/reservation/approve/" + reservationRequest.id()))
                .body(reservationService.approvePaymentWaiting(reservationRequest.id(), reservationRequest));
    }

    @Operation(summary = "사용자 예약 정보 조회 API")
    @GetMapping("/reservation/information/{id}")
    public ResponseEntity<ReservationInformResponse> getReservationInformation(@PathVariable long id) {
        ReservationInformResponse reservationInformResponse = reservationService.findById(id);
        return ResponseEntity.ok(reservationInformResponse);
    }
}
