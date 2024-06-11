package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.request.ReservationTimeRequest;
import roomescape.model.ReservationTime;
import roomescape.response.IsReservedTimeResponse;
import roomescape.response.ReservationTimeResponse;
import roomescape.service.ReservationTimeService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @GetMapping("/times")
    public ResponseEntity<List<ReservationTimeResponse>> getReservationTimes() {
        List<ReservationTime> reservationTimes = reservationTimeService.findAllReservationTimes();
        List<ReservationTimeResponse> responses = reservationTimes.stream()
                .map(time -> new ReservationTimeResponse(time.getId(), time.getStartAt()))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> createReservationTime(@RequestBody ReservationTimeRequest request) {
        ReservationTime reservationTime = reservationTimeService.addReservationTime(request);
        ReservationTimeResponse response = new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
        return ResponseEntity.created(URI.create("/times/" + reservationTime.getId())).body(response);
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteReservationTime(@PathVariable("id") long id) {
        reservationTimeService.deleteReservationTime(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/times/reserved")
    public ResponseEntity<List<IsReservedTimeResponse>> getPossibleReservationTimes(
            @RequestParam(name = "date") LocalDate date,
            @RequestParam(name = "themeId") long themeId) {
        List<IsReservedTimeResponse> response = reservationTimeService.getIsReservedTime(date, themeId);
        return ResponseEntity.ok(response);
    }
}
