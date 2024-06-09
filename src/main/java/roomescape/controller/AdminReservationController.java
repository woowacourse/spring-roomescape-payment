package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.AdminReservationDetailResponse;
import roomescape.dto.AdminReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.service.ReservationService;

import java.net.URI;
import java.util.List;

@Tag(name = "관리자 예약 API", description = "관리자 예약 API 입니다.")
@RestController
public class AdminReservationController {
    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "관리자 예약", description = "관리자가 예약을 추가합니다.")
    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByAdmin(
            @RequestBody AdminReservationRequest reservationRequest) {
        ReservationResponse reservationResponse = reservationService.saveByAdmin(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "관리자 예약 대기", description = "관리자가 예약 대기를 추가합니다.")
    @GetMapping("/admin/reservations-waiting")
    public List<AdminReservationDetailResponse> findAllWaitingReservations() {
        return reservationService.findAllWaitingReservations();
    }

    @Operation(summary = "관리자 예약 대기 삭제", description = "관리자가 예약 대기를 삭제합니다.")
    @DeleteMapping("/admin/reservations-waiting/{id}")
    public ResponseEntity<Void> deleteReservationWaitingByAdmin(@PathVariable long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
