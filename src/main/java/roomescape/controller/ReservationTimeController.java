package roomescape.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import roomescape.dto.AvailableTimeResponse;
import roomescape.dto.ReservationTimeRequest;
import roomescape.dto.ReservationTimeResponse;
import roomescape.service.ReservationTimeService;

@Tag(name = "예약 시간 API", description = "예약 시간 API 입니다.")
@RestController
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 시간 추가", description = "예약 시간을 추가합니다.")
    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> save(@RequestBody ReservationTimeRequest reservationTimeRequest) {
        ReservationTimeResponse savedReservationTimeResponse = reservationTimeService.save(reservationTimeRequest);
        return ResponseEntity.created(URI.create("/times/" + savedReservationTimeResponse.id()))
                .body(savedReservationTimeResponse);
    }

    @Operation(summary = "예약 시간 조회", description = "전체 예약 시간을 조회합니다.")
    @GetMapping("/times")
    public List<ReservationTimeResponse> findAll() {
        return reservationTimeService.findAll();
    }

    @Operation(summary = "예약 가능한 예약 시간 조회", description = "예약 가능한 예약 시간을 조회합니다.")
    @GetMapping("/times/available")
    public List<AvailableTimeResponse> findAvailableTimeWith(@RequestParam LocalDate date, @RequestParam long themeId) {
        return reservationTimeService.findByThemeAndDate(date, themeId);
    }

    @Operation(summary = "예약 시간 삭제", description = "예약 시간을 삭제합니다.")
    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
