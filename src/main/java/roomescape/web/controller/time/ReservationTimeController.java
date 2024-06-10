package roomescape.web.controller.time;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import roomescape.dto.time.ReservationTimeRequest;
import roomescape.dto.time.ReservationTimeResponse;
import roomescape.dto.time.TimeWithAvailableResponse;
import roomescape.service.time.TimeDeleteService;
import roomescape.service.time.TimeRegisterService;
import roomescape.service.time.TimeSearchService;

@Tag(name = "예약 시간 관리")
@RestController
@RequestMapping("/times")
class ReservationTimeController {

    private final TimeRegisterService registerService;
    private final TimeSearchService searchService;
    private final TimeDeleteService deleteService;

    public ReservationTimeController(TimeRegisterService registerService,
                                     TimeSearchService searchService,
                                     TimeDeleteService deleteService
    ) {
        this.registerService = registerService;
        this.searchService = searchService;
        this.deleteService = deleteService;
    }

    @Operation(summary = "예약 시간 등록", description = "요청한 시간으로 예약 가능한 시간대를 등록한다.")
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> createReservationTime(@RequestBody ReservationTimeRequest request) {
        ReservationTimeResponse response = registerService.registerTime(request);
        return ResponseEntity.created(URI.create("/times/" + response.id())).body(response);
    }

    @Operation(summary = "예약 시간 조회", description = "등록된 예약 시간대 중 특정 예약 시간을 조회한다.")
    @GetMapping("/{id}")
    public ResponseEntity<ReservationTimeResponse> getReservationTime(@PathVariable Long id) {
        ReservationTimeResponse response = searchService.findTime(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 예약 시간 조회", description = "등록된 모든 예약 시간대를 조회한다.")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> getAllReservationTimes() {
        List<ReservationTimeResponse> responses = searchService.findAllTimes();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 가능 시간 조회", description = "특정 날짜와 테마에 예약이 없는 시간대를 조회한다.")
    @GetMapping("/available")
    public ResponseEntity<List<TimeWithAvailableResponse>> getAvailableTimes(@RequestParam LocalDate date,
                                                                             @RequestParam Long themeId
    ) {
        List<TimeWithAvailableResponse> responses = searchService.findAvailableTimes(date, themeId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 시간 삭제", description = "등록된 예약 시간대를 삭제한다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationTime(@PathVariable Long id) {
        deleteService.deleteTime(id);
        return ResponseEntity.noContent().build();
    }
}
