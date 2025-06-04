package roomescape.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.api.AdminTimeApi;
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.service.reservation.ReservationTimeService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminTimeController implements AdminTimeApi {

    private final ReservationTimeService reservationTimeService;

    @Override
    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> save(@RequestBody @Valid ReservationTimeRequest request) {
        ReservationTimeResponse response = reservationTimeService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/times")
    public ResponseEntity<List<ReservationTimeResponse>> getAll() {
        List<ReservationTimeResponse> response = reservationTimeService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @DeleteMapping("/times/{timeId}")
    public ResponseEntity<Void> remove(@PathVariable long timeId) {
        reservationTimeService.remove(timeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
