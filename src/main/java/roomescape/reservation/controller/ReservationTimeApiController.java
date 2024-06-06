package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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
import roomescape.common.dto.MultipleResponses;
import roomescape.reservation.dto.AvailableReservationTimeResponse;
import roomescape.reservation.dto.TimeResponse;
import roomescape.reservation.dto.TimeSaveRequest;
import roomescape.reservation.service.ReservationTimeService;

@Tag(name = "예약 시간 API", description = "예약 시간 API 입니다.")
@RestController
public class ReservationTimeApiController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeApiController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 시간 조회 API", description = "예약 가능한 시간을 조회 합니다.")
    @GetMapping("/times")
    public ResponseEntity<MultipleResponses<TimeResponse>> findAll() {
        List<TimeResponse> times = reservationTimeService.findAll();

        return ResponseEntity.ok(new MultipleResponses<>(times));
    }

    @Operation(summary = "테마 예약 시간 예약 가능 여부 확인 API", description = "해당 날짜에 테마 예약 시간 별 예약 가능 여부를 조회 합니다.")
    @Parameter(name = "date", description = "확인할 날짜", schema = @Schema(type = "string", format = "date", example = "2024-06-07"))
    @Parameter(name = "theme-id", description = "확인할 테마 id", schema = @Schema(type = "integer", example = "1"))
    @GetMapping("/times/available")
    public ResponseEntity<MultipleResponses<AvailableReservationTimeResponse>> findAvailableTimes(
            @RequestParam("date") LocalDate date,
            @RequestParam("theme-id") Long themeId
    ) {
        List<AvailableReservationTimeResponse> availableTimes = reservationTimeService.findAvailableTimes(date, themeId);

        return ResponseEntity.ok(new MultipleResponses<>(availableTimes));
    }

    @Operation(summary = "예약 시간 추가 API", description = "예약 시간을 추가 합니다.")
    @PostMapping("/times")
    public ResponseEntity<TimeResponse> save(@Valid @RequestBody TimeSaveRequest timeSaveRequest) {
        TimeResponse timeResponse = reservationTimeService.save(timeSaveRequest);

        return ResponseEntity.created(URI.create("/times/" + timeResponse.id())).body(timeResponse);
    }

    @Operation(summary = "예약 시간 삭제 API", description = "예약 시간을 삭제 합니다.")
    @Parameter(name = "id", description = "삭제할 예약 시간의 id", schema = @Schema(type = "integer", example = "1"))
    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        reservationTimeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
