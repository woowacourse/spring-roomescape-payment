package roomescape.reservationtime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservationtime.dto.request.CreateReservationTimeRequest;
import roomescape.reservationtime.dto.response.CreateReservationTimeResponse;
import roomescape.reservationtime.dto.response.FindReservationTimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;

@Tag(name = "예약 시간 API", description = "예약 시간 관련 API")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 시간 생성 API")
    @PostMapping
    public ResponseEntity<CreateReservationTimeResponse> createReservationTime(@Valid @RequestBody final CreateReservationTimeRequest createReservationTimeRequest) {
        CreateReservationTimeResponse reservationTime = reservationTimeService.createReservationTime(createReservationTimeRequest);
        return ResponseEntity.created(URI.create("/times/" + reservationTime.id())).body(reservationTime);
    }

    @Operation(summary = "예약 시간 목록 조회 API")
    @GetMapping
    public ResponseEntity<List<FindReservationTimeResponse>> getReservationTimes() {
        List<FindReservationTimeResponse> reservationTimeResponses = reservationTimeService.getReservationTimes();
        return ResponseEntity.ok(reservationTimeResponses);
    }

    @Operation(summary = "예약 시간 조회 API")
    @GetMapping("/{id}")
    public ResponseEntity<FindReservationTimeResponse> getReservationTime(@PathVariable final Long id) {
        return ResponseEntity.ok(reservationTimeService.getReservationTime(id));
    }

    @Operation(summary = "예약 시간 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationTime(@PathVariable final Long id) {
        reservationTimeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
