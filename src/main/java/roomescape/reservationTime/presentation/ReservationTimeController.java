package roomescape.reservationTime.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.reservationTime.adaptor.ReservationTimeApiAdaptor;
import roomescape.reservationTime.docs.ReservationTimeRequestDocs;
import roomescape.reservationTime.docs.ReservationTimeResponseDocs;
import roomescape.reservationTime.docs.TimeConditionRequestDocs;
import roomescape.reservationTime.docs.TimeConditionResponseDocs;
import roomescape.reservationTime.dto.request.ReservationTimeRequest;
import roomescape.reservationTime.dto.request.TimeConditionRequest;
import roomescape.reservationTime.dto.response.ReservationTimeResponse;
import roomescape.reservationTime.dto.response.TimeConditionResponse;
import roomescape.reservationTime.service.ReservationTimeService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(ReservationTimeController.RESERVATION_TIME_BASE_URL)
public class ReservationTimeController {

    public static final String RESERVATION_TIME_BASE_URL = "/times";
    private static final String SLASH = "/";

    private final ReservationTimeService reservationTimeService;
    private final ReservationTimeApiAdaptor reservationTimeApiAdaptor;

    public ReservationTimeController(final ReservationTimeService reservationTimeService, final ReservationTimeApiAdaptor reservationTimeApiAdaptor) {
        this.reservationTimeService = reservationTimeService;
        this.reservationTimeApiAdaptor = reservationTimeApiAdaptor;
    }

    @PostMapping
    public ResponseEntity<ReservationTimeResponseDocs> createReservationTime(
            @RequestBody final ReservationTimeRequestDocs requestDocs) {
        ReservationTimeRequest request = reservationTimeApiAdaptor.toReservationTimeRequest(requestDocs);
        ReservationTimeResponse response = reservationTimeService.createReservationTime(request);
        ReservationTimeResponseDocs responseDocs = reservationTimeApiAdaptor.toReservationTimeResponseDocs(response);

        URI locationUri = URI.create(RESERVATION_TIME_BASE_URL + SLASH + response.id());
        return ResponseEntity.created(locationUri).body(responseDocs);
    }

    @GetMapping
    public ResponseEntity<List<ReservationTimeResponseDocs>> getReservationTimes() {
        List<ReservationTimeResponse> response = reservationTimeService.getReservationTimes();
        List<ReservationTimeResponseDocs> responseDocs = response.stream()
                .map(reservationTimeApiAdaptor::toReservationTimeResponseDocs)
                .toList();
        return ResponseEntity.ok(responseDocs);
    }

    @GetMapping(consumes = {"application/json"})
    public ResponseEntity<List<TimeConditionResponseDocs>> getReservationTimes(
            final TimeConditionRequestDocs requestDocs) {
        TimeConditionRequest request = reservationTimeApiAdaptor.toTimeConditionRequest(requestDocs);
        List<TimeConditionResponse> responses = reservationTimeService.getTimesWithCondition(request);
        List<TimeConditionResponseDocs> responseDocs = responses.stream()
                .map(reservationTimeApiAdaptor::toTimeConditionResponseDocs)
                .toList();
        return ResponseEntity.ok().body(responseDocs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationTimeById(@PathVariable("id") final Long id) {
        reservationTimeService.deleteReservationTimeById(id);
        return ResponseEntity.noContent().build();
    }
}
