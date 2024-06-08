package roomescape.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.AvailableTimeResponse;
import roomescape.dto.ReservationTimeRequest;
import roomescape.dto.ReservationTimeResponse;
import roomescape.service.ReservationTimeService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Reservation Time", description = "예약 시간 API")
@RestController
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> save(@RequestBody ReservationTimeRequest reservationTimeRequest) {
        ReservationTimeResponse savedReservationTimeResponse = reservationTimeService.save(reservationTimeRequest);
        return ResponseEntity.created(URI.create("/times/" + savedReservationTimeResponse.id()))
                .body(savedReservationTimeResponse);
    }

    @GetMapping("/times")
    public List<ReservationTimeResponse> findAll() {
        return reservationTimeService.findAll();
    }

    @GetMapping("/times/available")
    public List<AvailableTimeResponse> findAvailableTimeWith(@RequestParam LocalDate date, @RequestParam long themeId) {
        return reservationTimeService.findByThemeAndDate(date, themeId);
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
