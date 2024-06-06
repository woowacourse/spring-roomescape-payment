package roomescape.reservation.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.reservation.controller.dto.AdminReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.WaitingReservationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/admin/reservations")
@Tag(name = "Admin Reservation API", description = "어드민 예약 관련 API입니다.")
public class AdminReservationController {

    private final ReservationService reservationService;
    private final WaitingReservationService waitingReservationService;

    public AdminReservationController(ReservationService reservationService,
                                      WaitingReservationService waitingReservationService) {
        this.reservationService = reservationService;
        this.waitingReservationService = waitingReservationService;
    }

    @PostMapping()
    @Operation(summary = "예약을 생성한다.", description = "결제 요구 없이 예약을 생성한다.")
    public ResponseEntity<ReservationResponse> create(
            @RequestBody @Valid AdminReservationRequest adminReservationRequest) {
        ReservationResponse reservationResponse = reservationService
                .createReservation(adminReservationRequest.toReservationRequest(), adminReservationRequest.memberId(), ReservationStatus.BOOKED); // TODO 상태도 요청으로 받기
        return ResponseEntity.created(URI.create("/admin/reservations/" + reservationResponse.reservationId()))
                .body(reservationResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "예약을 삭제한다.")
    public void delete(@PathVariable("id") @Min(1) long reservationId) {
        reservationService.delete(reservationId);
    }

    @GetMapping("/waiting")
    @Operation(summary = "대기중인 예약을 조회한다.")
    public List<ReservationResponse> waiting() {
        return waitingReservationService.findAllByWaitingReservation();
    }
}
