package roomescape.controller;

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
import roomescape.dto.AvailableTimeResponse;
import roomescape.dto.ReservationTimeRequest;
import roomescape.dto.ReservationTimeResponse;
import roomescape.service.AvailableTimeService;
import roomescape.service.ReservationTimeService;

@RestController
@RequestMapping(value = "/times")
@Tag(name = "예약 시간 API", description = "예약 시간 관련 API 입니다.")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;
    private final AvailableTimeService availableTimeService;

    public ReservationTimeController(
            ReservationTimeService reservationTimeService,
            AvailableTimeService availableTimeService
    ) {
        this.reservationTimeService = reservationTimeService;
        this.availableTimeService = availableTimeService;
    }

    @PostMapping
    @Operation(summary = "예약 시간 등록 API", description = "예약 시간을 등록합니다.")
    public ResponseEntity<ReservationTimeResponse> save(@RequestBody ReservationTimeRequest reservationTimeRequest) {
        ReservationTimeResponse saved = reservationTimeService.save(reservationTimeRequest);
        return ResponseEntity.created(URI.create("/times/" + saved.id()))
                .body(saved);
    }

    @GetMapping
    @Operation(summary = "예약 시간 목록 조회 API", description = "모든 예약 시간 목록을 조회합니다.")
    public List<ReservationTimeResponse> findAll() {
        return reservationTimeService.findAll();
    }

    @GetMapping("/book-able")
    @Operation(summary = "예약 가능 시간 목록 조회 API", description = "예약 가능한 시간 목록을 조회합니다.")
    public List<AvailableTimeResponse> findByThemeAndDate(@RequestParam LocalDate date, @RequestParam long themeId) {
        return availableTimeService.findByThemeAndDate(date, themeId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "예약 시간 삭제 API", description = "예약 시간을 삭제합니다.")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
