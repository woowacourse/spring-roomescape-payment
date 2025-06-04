package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.service.reservation.ReservationTimeService;

@Tag(name = "5. 어드민 전용 API")
@RequiredArgsConstructor
@RestController
public class AdminReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    @Operation(summary = "예약 시간 추가")
    @PostMapping("/admin/times")
    public ResponseEntity<ReservationTimeResponse> save(@RequestBody @Valid ReservationTimeRequest request) {
        ReservationTimeResponse response = reservationTimeService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "예약 시간 삭제")
    @DeleteMapping("/admin/times/{timeId}")
    public ResponseEntity<Void> remove(@PathVariable long timeId) {
        reservationTimeService.remove(timeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
