package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.service.ReservationTimeService;
import roomescape.reservation.service.dto.request.ReservationTimeRequest;
import roomescape.reservation.service.dto.response.ReservationTimeResponse;

import java.net.URI;
import java.util.List;

@RequestMapping("/times")
@RestController
@Tag(name = "예약 시간 컨트롤러", description = "예약 시간에 관한 API 모음")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @GetMapping
    @Operation(summary = "예약 시간 목록 조회", description = "모든 예약 시간 목록을 가져옵니다.")
    public ResponseEntity<List<ReservationTimeResponse>> readAllReservationTimes() {
        List<ReservationTimeResponse> responses = reservationTimeService.getAll();

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @Operation(summary = "예약 시간 생성", description = "입력받은 예약 시간을 생성합니다.")
    public ResponseEntity<ReservationTimeResponse> create(@Valid @RequestBody final ReservationTimeRequest request) {
        ReservationTimeResponse response = reservationTimeService.create(request);

        return ResponseEntity.created(URI.create("/times/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "예약 시간 삭제", description = "선택한 예약 시간을 삭제합니다.")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        reservationTimeService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
