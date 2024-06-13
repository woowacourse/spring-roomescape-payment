package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.controller.dto.AvailableTimeResponse;
import roomescape.reservation.controller.dto.ReservationTimeRequest;
import roomescape.reservation.controller.dto.ReservationTimeResponse;
import roomescape.reservation.service.ReservationTimeService;

@Tag(name = "Reservation Time", description = "Reservation Time API")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @PostMapping
    public ResponseEntity<ReservationTimeResponse> create(
            @RequestBody @Valid ReservationTimeRequest reservationTimeRequest) {
        ReservationTimeResponse response = reservationTimeService.create(reservationTimeRequest);
        return ResponseEntity.created(URI.create("/times/" + response.id())).body(response);
    }

    @GetMapping
    public List<ReservationTimeResponse> findAll() {
        return reservationTimeService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") @Min(1) long timeId) {
        reservationTimeService.delete(timeId);
    }

    @GetMapping("/available")
    public List<AvailableTimeResponse> getAvailable(
            @RequestParam("date") @Future LocalDate date,
            @RequestParam("themeId") @Min(1) long themeId) {
        return reservationTimeService.findAvailableTimes(date, themeId);
    }
}
