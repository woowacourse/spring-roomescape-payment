package roomescape.controller.api;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.request.CreateTimeRequest;
import roomescape.controller.dto.response.TimeResponse;
import roomescape.service.ReservationTimeService;

@Tag(name = "AdminReservationTime", description = "관리자만 접근할 수 있는 방탈출 예약 가능 시간 관련 API")
@RestController
@RequestMapping("/admin/times")
public class AdminReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public AdminReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "모든 시간 조회", description = "모든 예약 가능 시간을 조회할 수 있다.")
    @GetMapping
    public ResponseEntity<List<TimeResponse>> findAll() {
        List<TimeResponse> response = reservationTimeService.findAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "시간 생성", description = "예약 가능 시간을 생성할 수 있다.")
    @PostMapping
    public ResponseEntity<TimeResponse> save(@Valid @RequestBody CreateTimeRequest request) {
        TimeResponse response = reservationTimeService.save(request.startAt());
        return ResponseEntity.created(URI.create("/times/" + response.id()))
                .body(response);
    }

    @Operation(summary = "시간 삭제", description = "예약 가능 시간을 삭제할 수 있다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
