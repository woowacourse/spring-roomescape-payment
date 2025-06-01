package roomescape.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.service.reservation.ReservationTimeService;

@RequiredArgsConstructor
@RestController
public class AdminReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    @PostMapping("/admin/times")
    public ResponseEntity<ReservationTimeResponse> reservationTimeAdd(@RequestBody @Valid ReservationTimeRequest request) {
        ReservationTimeResponse response = reservationTimeService.addReservationTime(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/admin/times/{timeId}")
    public ResponseEntity<Void> reservationTimeRemove(@PathVariable long timeId) {
        reservationTimeService.removeReservationTime(timeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
