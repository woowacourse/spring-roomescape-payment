package roomescape.controller;

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
import roomescape.domain.reservationtime.ReservationTimeService;
import roomescape.domain.reservationtime.dto.AvailableReservationTimeResponse;
import roomescape.domain.reservationtime.dto.ReservationTimeRequest;
import roomescape.domain.reservationtime.dto.ReservationTimeResponse;

@Tag(
        name = "시간 컨트롤러"
)
@RestController
@RequestMapping("/times")
@AllArgsConstructor
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    @Operation(
            description = "예약 시간을 생성한다."
    )
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> create(
            @RequestBody @Valid final ReservationTimeRequest request
    ) {
        final ReservationTimeResponse response = reservationTimeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            description = "예약을 시간을 모두 조회한다."
    )
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> findAll() {
        final List<ReservationTimeResponse> response = reservationTimeService.findAll();
        return ResponseEntity.ok(response);
    }

    @Operation(
            description = "PENDING 상태인 예약은 체크하여 시간을 모두 조회한다."
    )
    @GetMapping("/available-time")
    public ResponseEntity<List<AvailableReservationTimeResponse>> findAllAvailableTimes(
            @RequestParam("themeId") final Long themeId,
            @RequestParam("date") final LocalDate date
    ) {
        final List<AvailableReservationTimeResponse> response = reservationTimeService
                .findAllAvailable(themeId, date);
        return ResponseEntity.ok(response);
    }

    @Operation(
            description = "예약 시간을 삭제한다. 사용중이라면 삭제가 불가능하다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") final Long id
    ) {
        reservationTimeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
