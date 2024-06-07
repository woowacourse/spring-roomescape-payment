package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.ReservationTimeService;
import roomescape.service.dto.request.ReservationTimeSaveRequest;
import roomescape.service.dto.response.ReservationTimeResponse;
import roomescape.service.dto.response.ReservationTimeResponses;

import java.net.URI;

@RestController
public class AdminReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public AdminReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "어드민 예약 시간 조회 API", description = "어드민이 예약 시간을 조회한다.")
    @GetMapping("/admin/times")
    public ResponseEntity<ReservationTimeResponses> getTimes() {
        ReservationTimeResponses reservationTimeResponses = reservationTimeService.getTimes();
        return ResponseEntity.ok(reservationTimeResponses);
    }

    @Operation(summary = "어드민 예약 시간 생성 API", description = "어드민이 예약 시간을 생성한다.")
    @PostMapping("/admin/times")
    public ResponseEntity<ReservationTimeResponse> saveTime(@RequestBody @Valid ReservationTimeSaveRequest reservationTimeSaveRequest) {
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.saveTime(reservationTimeSaveRequest);
        return ResponseEntity.created(URI.create("/times/" + reservationTimeResponse.id()))
                .body(reservationTimeResponse);
    }

    @Operation(summary = "어드민 예약 시간 삭제 API", description = "어드민이 예약 시간을 삭제한다.")
    @DeleteMapping("/admin/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable("id") Long id) {
        reservationTimeService.deleteTime(id);
        return ResponseEntity.noContent().build();
    }
}
