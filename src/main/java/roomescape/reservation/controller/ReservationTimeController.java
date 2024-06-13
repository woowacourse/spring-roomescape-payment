package roomescape.reservation.controller;

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
import roomescape.common.dto.ResourcesResponse;
import roomescape.reservation.controller.dto.request.TimeSaveRequest;
import roomescape.reservation.controller.dto.response.AvailableReservationTimeResponse;
import roomescape.reservation.controller.dto.response.TimeResponse;
import roomescape.reservation.service.module.ReservationTimeService;

@RestController
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @GetMapping("/times")
    public ResponseEntity<ResourcesResponse<TimeResponse>> findAll() {
        List<TimeResponse> times = reservationTimeService.findAll();
        ResourcesResponse<TimeResponse> response = new ResourcesResponse<>(times);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/times/available")
    public ResponseEntity<ResourcesResponse<AvailableReservationTimeResponse>> findAvailableTimes(
            @RequestParam("date") LocalDate date,
            @RequestParam("theme-id") Long themeId
    ) {
        List<AvailableReservationTimeResponse> times = reservationTimeService.findAvailableTimes(date, themeId);
        ResourcesResponse<AvailableReservationTimeResponse> response = new ResourcesResponse<>(times);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/times")
    public ResponseEntity<TimeResponse> save(@Valid @RequestBody TimeSaveRequest saveRequest) {
        TimeResponse response = reservationTimeService.save(saveRequest);

        return ResponseEntity.created(URI.create("/times/" + response.id())).body(response);
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        reservationTimeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
