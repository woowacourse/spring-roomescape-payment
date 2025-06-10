package roomescape.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.api.ReservationTimeRestControllerInterface;
import roomescape.domain.time.dto.ReservationTimeRequest;
import roomescape.domain.time.dto.ReservationTimeResponse;
import roomescape.domain.time.service.ReservationTimeServiceFacade;

@RequiredArgsConstructor
@RestController
public class ReservationTimeRestController implements ReservationTimeRestControllerInterface {

    private final ReservationTimeServiceFacade timeService;

    @Override
    public ResponseEntity<ReservationTimeResponse> createReservationTime(
            @RequestBody final ReservationTimeRequest request
    ) {

        final ReservationTimeResponse reservationTimeResponse = timeService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationTimeResponse);
    }

    @Override
    public ResponseEntity<List<ReservationTimeResponse>> getReservationTimes() {

        final List<ReservationTimeResponse> reservationTimeResponses = timeService.findAll();

        return ResponseEntity.ok(reservationTimeResponses);
    }

    @Override
    public ResponseEntity<Void> deleteReservationTime(
            @PathVariable final Long id
    ) {
        timeService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
