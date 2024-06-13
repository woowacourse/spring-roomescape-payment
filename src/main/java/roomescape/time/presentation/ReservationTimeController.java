package roomescape.time.presentation;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.time.dto.AvailableTimeResponse;
import roomescape.time.dto.ReservationTimeAddRequest;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.service.ReservationTimeService;

@RestController
@Tag(name = "ReservationTime API", description = "예약 시간 관련 API")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "전체 예약 시간 조회 API", description = "전체 예약 시간을 조회합니다.")
    @GetMapping("/times")
    public ResponseEntity<List<ReservationTimeResponse>> getReservationTimeList() {
        return ResponseEntity.ok(reservationTimeService.findAllReservationTime());
    }

    @Operation(summary = "예약 시간 생성 API", description = "예약 시간을 생성합니다.")
    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> saveReservationTime(
            @Valid @RequestBody ReservationTimeAddRequest reservationTimeAddRequest) {
        ReservationTimeResponse saveResponse = reservationTimeService.saveReservationTime(reservationTimeAddRequest);
        URI createdUri = URI.create("/times/" + saveResponse.id());
        return ResponseEntity.created(createdUri).body(saveResponse);
    }

    @Operation(summary = "예약 가능 시간 조회 API", description = "예약 가능 시간을 조회합니다.")
    @GetMapping("/times/available")
    public ResponseEntity<List<AvailableTimeResponse>> readTimesStatus(
            @RequestParam(name = "date") LocalDate date,
            @RequestParam(name = "themeId") Long themeId) {
        return ResponseEntity.ok(reservationTimeService.findAllWithBookStatus(date, themeId));
    }

    @Operation(summary = "예약 시간 삭제 API", description = "예약 시간을 삭제합니다.")
    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> removeReservationTime(@PathVariable("id") Long id) {
        reservationTimeService.removeReservationTime(id);
        return ResponseEntity.noContent().build();
    }
}
