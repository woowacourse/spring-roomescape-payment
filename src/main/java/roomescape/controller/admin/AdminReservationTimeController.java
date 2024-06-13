package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "어드민 예약 시간 API", description = "어드민 예약 시간 관련 API 입니다.")
@RestController
@RequestMapping("/admin")
public class AdminReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public AdminReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "어드민 예약 시간 추가 API")
    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> create(@RequestBody ReservationTimeRequest reservationTimeRequest) {
        ReservationTimeResponse response = reservationTimeService.save(reservationTimeRequest);
        URI location = URI.create("/times/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "어드민 예약 시간 삭제 API")
    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable long id) {
        reservationTimeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
