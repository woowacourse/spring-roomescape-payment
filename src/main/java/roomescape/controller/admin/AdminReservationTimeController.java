package roomescape.controller.admin;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.ReservationTimeService;
import roomescape.dto.request.reservation.ReservationTimeRequest;
import roomescape.dto.response.reservation.ReservationTimeResponse;

@RestController
@RequestMapping("/admin")
public class AdminReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public AdminReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> saveTime(@RequestBody ReservationTimeRequest reservationTimeRequest) {
        ReservationTimeResponse response = reservationTimeService.save(reservationTimeRequest);
        URI location = URI.create("/times/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable long id) {
        reservationTimeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
