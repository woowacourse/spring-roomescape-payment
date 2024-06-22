package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.reservation.controller.dto.AvailableTimeResponse;
import roomescape.reservation.controller.dto.ReservationTimeRequest;
import roomescape.reservation.controller.dto.ReservationTimeResponse;
import roomescape.reservation.service.ReservationTimeService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/times")
@Tag(name = "Time API", description = "예약 시간 관련 API")
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @PostMapping
    @Operation(summary = "예약 시간을 생성한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "이미 존재하는 시간을 생성하려는 경우 발생")
    })
    @Parameter(name = "reservationTimeRequest", description = "예약 시간 DTO", required = true)
    public ResponseEntity<ReservationTimeResponse> create(
            @RequestBody @Valid ReservationTimeRequest reservationTimeRequest) {
        ReservationTimeResponse response = reservationTimeService.create(reservationTimeRequest);
        return ResponseEntity.created(URI.create("/times/" + response.id())).body(response);
    }

    @GetMapping
    @Operation(summary = "예약 시간들을 모두 조회한다.")
    @ApiResponse(responseCode = "200", description = "OK")
    public List<ReservationTimeResponse> findAll() {
        return reservationTimeService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "예약 시간을 조회한다.")
    @Parameter(name = "id", description = "예약 시간 ID", required = true)
    public void delete(@PathVariable("id") @Min(1) long timeId) {
        reservationTimeService.delete(timeId);
    }

    @GetMapping("/available")
    @Operation(summary = "예약 가능한 시간들을 조회한다.")
    @Parameters({
        @Parameter(name = "date", description = "조회하고 하는 날짜", required = true),
        @Parameter(name = "themeId", description = "테마 ID", required = true)
    })
    public List<AvailableTimeResponse> getAvailable(
            @RequestParam("date") @Future LocalDate date,
            @RequestParam("themeId") @Min(1) long themeId) {
        return reservationTimeService.findAvailableTimes(date, themeId);
    }
}
