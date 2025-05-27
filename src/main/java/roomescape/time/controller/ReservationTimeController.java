package roomescape.time.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.time.controller.dto.AvailableReservationTimeRequest;
import roomescape.time.controller.dto.AvailableReservationTimeResponse;
import roomescape.time.controller.dto.CreateReservationTimeRequest;
import roomescape.time.controller.dto.ReservationTimeResponse;

@RestController
@RequestMapping("/times")
public class ReservationTimeController {
    private final TimeService timeService;

    public ReservationTimeController(final TimeService timeService) {
        this.timeService = timeService;
    }

    @PostMapping
    public ResponseEntity<ReservationTimeResponse> createTime(
            @RequestBody @Valid final CreateReservationTimeRequest request
    ) {
        ReservationTimeResponse response = timeService.createReservationTime(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> getTimes() {
        List<ReservationTimeResponse> responses = timeService.findAllReservationTimes();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable final Long id) {
        timeService.deleteReservationTimeById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<AvailableReservationTimeResponse>> getAvailableReservationTimes(
            @ModelAttribute @Valid final AvailableReservationTimeRequest request
    ) {
        List<AvailableReservationTimeResponse> responses = timeService.findAvailableReservationTimes(request);
        return ResponseEntity.ok(responses);
    }
}
