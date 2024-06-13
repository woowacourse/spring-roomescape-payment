package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.ReservationTimeService;
import roomescape.service.dto.request.ReservationTimeBookedRequest;
import roomescape.service.dto.response.ReservationTimeBookedResponses;
import roomescape.service.dto.response.ReservationTimeResponses;

@Tag(name = "[USER] 예약 시간 API", description = "사용자가 예약 시간을 조회할 수 있습니다.")
@RestController
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 시간 조회 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "전체 예약 시간을 반환합니다.")
    })
    @GetMapping("/times")
    public ResponseEntity<ReservationTimeResponses> getTimes() {
        ReservationTimeResponses reservationTimeResponses = reservationTimeService.getTimes();
        return ResponseEntity.ok(reservationTimeResponses);
    }

    @Operation(summary = "예약가능 시간 조회 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "예약 가능한 시간을 반환합니다.")
    })
    @GetMapping("/times/booked")
    public ResponseEntity<ReservationTimeBookedResponses> getTimesWithBooked(@ModelAttribute @Valid ReservationTimeBookedRequest reservationTimeBookedRequest) {
        ReservationTimeBookedResponses reservationTimeBookedResponses = reservationTimeService.getTimesWithBooked(reservationTimeBookedRequest);
        return ResponseEntity.ok(reservationTimeBookedResponses);
    }
}
