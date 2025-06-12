package roomescape.reservationtime;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservationtime.dto.AvailableReservationTimeResponse;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;

@RestController
@RequestMapping("/times")
@AllArgsConstructor
@Tag(name = "예약 시간 관련 API", description = "예약 시간을 생성하고 조회하는 API입니다.")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    @Operation(summary = "예약 시간 생성", description = "새로운 예약 시간을 생성합니다.")
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> create(
            @RequestBody @Valid final ReservationTimeRequest request
    ) {
        final ReservationTimeResponse response = reservationTimeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "모든 예약 시간 조회", description = "등록된 모든 예약 시간을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> findAll() {
        final List<ReservationTimeResponse> response = reservationTimeService.findAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예약 가능 시간 조회", description = "예약 가능 시간을 조회합니다.")
    @GetMapping("/available-time")
    public ResponseEntity<List<AvailableReservationTimeResponse>> findAllAvailableTimes(
            @RequestParam("themeId") final Long themeId,
            @RequestParam("date") final LocalDate date
    ) {
        final List<AvailableReservationTimeResponse> response = reservationTimeService
                .findAllAvailable(themeId, date);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 예약 시간 삭제", description = "ID로 특정 예약 시간을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") final Long id
    ) {
        reservationTimeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
