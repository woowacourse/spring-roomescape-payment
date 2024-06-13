package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.ReservationTimeService;
import roomescape.service.dto.request.ReservationTimeSaveRequest;
import roomescape.service.dto.response.ReservationTimeResponse;
import roomescape.service.dto.response.ReservationTimeResponses;

import java.net.URI;

@Tag(name = "[ADMIN] 예약 시간 API", description = "어드민 권한으로 예약시간을 생성/조회/삭제할 수 있습니다.")
@RestController
public class AdminReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public AdminReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "어드민 예약 시간 조회 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "전체 예약 시간을 반환합니다.")
    })
    @GetMapping("/admin/times")
    public ResponseEntity<ReservationTimeResponses> getTimes() {
        ReservationTimeResponses reservationTimeResponses = reservationTimeService.getTimes();
        return ResponseEntity.ok(reservationTimeResponses);
    }

    @Operation(summary = "어드민 예약 시간 생성 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "생성된 예약 시간을 반환합니다.")
    })
    @PostMapping("/admin/times")
    public ResponseEntity<ReservationTimeResponse> saveTime(@RequestBody @Valid ReservationTimeSaveRequest reservationTimeSaveRequest) {
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.saveTime(reservationTimeSaveRequest);
        return ResponseEntity.created(URI.create("/times/" + reservationTimeResponse.id()))
                .body(reservationTimeResponse);
    }

    @Operation(summary = "어드민 예약 시간 삭제 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "204", description = "예약 시간 삭제에 성공했습니다.")
    })
    @DeleteMapping("/admin/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable("id") Long id) {
        reservationTimeService.deleteTime(id);
        return ResponseEntity.noContent().build();
    }
}
