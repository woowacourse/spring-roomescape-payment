package roomescape.web.controller.api;

import jakarta.validation.Valid;
import java.net.URI;
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
import roomescape.service.ReservationTimeService;
import roomescape.service.request.ReservationTimeSaveAppRequest;
import roomescape.service.response.BookableReservationTimeAppResponse;
import roomescape.service.response.ReservationTimeAppResponse;
import roomescape.web.controller.request.ReservationTimeRequest;
import roomescape.web.controller.response.BookableReservationTimeResponse;
import roomescape.web.controller.response.ReservationTimeResponse;

@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @PostMapping
    public ResponseEntity<ReservationTimeResponse> create(@Valid @RequestBody ReservationTimeRequest request) {
        ReservationTimeAppResponse appResponse = reservationTimeService.save(
                new ReservationTimeSaveAppRequest(request.startAt()));
        Long id = appResponse.id();

        return ResponseEntity.created(URI.create("/times/" + id))
                .body(new ReservationTimeResponse(id, appResponse.startAt()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBy(@PathVariable Long id) {
        reservationTimeService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> getReservationTimes() {
        List<ReservationTimeAppResponse> appResponses = reservationTimeService.findAll();

        List<ReservationTimeResponse> reservationTimeResponses = appResponses.stream()
                .map(ReservationTimeResponse::from)
                .toList();

        return ResponseEntity.ok(reservationTimeResponses);
    }

    @GetMapping("/availability")
    public ResponseEntity<List<BookableReservationTimeResponse>> getReservationTimesWithAvailability(
            @RequestParam String date,
            @RequestParam Long id) {

        List<BookableReservationTimeAppResponse> appResponses = reservationTimeService
                .findAllWithBookAvailability(date, id);

        List<BookableReservationTimeResponse> webResponses = appResponses.stream()
                .map(BookableReservationTimeResponse::from)
                .toList();

        return ResponseEntity.ok(webResponses);
    }
}
