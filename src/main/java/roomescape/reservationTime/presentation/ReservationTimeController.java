package roomescape.reservationTime.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.reservationTime.dto.request.ReservationTimeRequest;
import roomescape.reservationTime.dto.request.TimeConditionRequest;
import roomescape.reservationTime.dto.response.ReservationTimeResponse;
import roomescape.reservationTime.dto.response.TimeConditionResponse;
import roomescape.reservationTime.service.ReservationTimeService;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ReservationTimeController.RESERVATION_TIME_BASE_URL)
public class ReservationTimeController implements ReservationTimeControllerDocs {

    public static final String RESERVATION_TIME_BASE_URL = "/times";
    private static final String SLASH = "/";

    private final ReservationTimeService reservationTimeService;

    @Override
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> createReservationTime(
            @RequestBody final ReservationTimeRequest request) {
        ReservationTimeResponse response = reservationTimeService.createReservationTime(request);

        URI locationUri = URI.create(RESERVATION_TIME_BASE_URL + SLASH + response.id());
        return ResponseEntity.created(locationUri).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> getReservationTimes() {
        List<ReservationTimeResponse> response = reservationTimeService.getReservationTimes();
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping(consumes = {"application/json"})
    public ResponseEntity<List<TimeConditionResponse>> getReservationTimes(
            final TimeConditionRequest request) {
        List<TimeConditionResponse> responses = reservationTimeService.getTimesWithCondition(request);
        return ResponseEntity.ok().body(responses);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationTimeById(@PathVariable("id") final Long id) {
        reservationTimeService.deleteReservationTimeById(id);
        return ResponseEntity.noContent().build();
    }
}
