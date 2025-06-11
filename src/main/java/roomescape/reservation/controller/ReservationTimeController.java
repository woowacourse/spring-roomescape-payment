package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.dto.response.AvailableReservationTimeResponse;
import roomescape.reservation.dto.response.ReservationTimeResponse;
import roomescape.reservation.service.ReservationTimeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/times")
@Tag(name = "예약 시간", description = "예약 시간 관련 API")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    @Operation(summary = "모든 예약 시간 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @SecurityRequirements(value = {})
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> getAllTimes() {
        List<ReservationTimeResponse> responses = reservationTimeService.getAllTimes();
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "모든 가능한 예약 시간 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @SecurityRequirements(value = {})
    @GetMapping("/available")
    public ResponseEntity<List<AvailableReservationTimeResponse>> getAvailableTimes(
            @RequestParam("date") LocalDate date,
            @RequestParam("themeId") long themeId
    ) {
        List<AvailableReservationTimeResponse> responses = reservationTimeService.getAvailableTimes(date, themeId);
        return ResponseEntity.ok().body(responses);
    }
}
