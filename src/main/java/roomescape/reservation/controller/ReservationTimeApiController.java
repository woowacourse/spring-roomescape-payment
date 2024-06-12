package roomescape.reservation.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import roomescape.reservation.dto.request.TimeCreateRequest;
import roomescape.reservation.dto.response.AvailableReservationTimeResponse;
import roomescape.reservation.dto.response.TimeResponse;
import roomescape.reservation.service.ReservationTimeService;

@RestController
public class ReservationTimeApiController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeApiController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @GetMapping("/times")
    public ResponseEntity<List<TimeResponse>> findAll() {
        List<TimeResponse> responses = reservationTimeService.findAll();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/times/available")
    public ResponseEntity<List<AvailableReservationTimeResponse>> findAvailableTimes(
            @RequestParam("date") LocalDate date,
            @RequestParam("theme-id") Long themeId
    ) {
        List<AvailableReservationTimeResponse> responses = reservationTimeService.findAvailableTimes(date,
                themeId);

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/times")
    public ResponseEntity<TimeResponse> save(@Valid @RequestBody TimeCreateRequest request) {
        Long saveId = reservationTimeService.save(request);
        TimeResponse response = reservationTimeService.findById(saveId);

        return ResponseEntity.created(URI.create("/times/" + saveId)).body(response);
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        reservationTimeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
