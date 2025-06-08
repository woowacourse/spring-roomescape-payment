package roomescape.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.dto.response.TimeWithBookedResponse;
import roomescape.service.ReservationTimeService;

@Tag(name = "예약 시간 API", description = "예약 시간 API 입니다.")
@RestController
@RequestMapping(value = "/api/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "모든 예약시간 조회", description = "모든 예약 시간을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> getAllReservationTimes() {
        List<ReservationTimeResponse> all = reservationTimeService.findAllTimes();
        return ResponseEntity.ok(all);
    }

    @Operation(summary = "예약시간 생성", description = "특정 예약 시간을 생성합니다.")
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> createNewReservationTime(
            @Valid @RequestBody ReservationTimeRequest reservationTimeRequest) {
        ReservationTimeResponse reservationTime = reservationTimeService.createTime(reservationTimeRequest);
        return ResponseEntity
                .created(URI.create("/api/times/" + reservationTime.id()))
                .body(reservationTime);
    }

    @Operation(summary = "예약 시간 삭제", description = "특정 예약 시간을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationTime(@PathVariable Long id) {
        reservationTimeService.deleteTimeById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약시간, 예약여부 조회", description = "모든 예약 시간과 해당 시간 예약 여부를 함께 조회합니다.")
    @GetMapping("theme/{themeId}")
    public ResponseEntity<List<TimeWithBookedResponse>> getTimesWithBooked(
            @PathVariable("themeId") Long themeId, @RequestParam("date") LocalDate date
    ) {
        return ResponseEntity.ok(reservationTimeService.findTimesByDateAndThemeIdWithBooked(date, themeId));
    }
}
