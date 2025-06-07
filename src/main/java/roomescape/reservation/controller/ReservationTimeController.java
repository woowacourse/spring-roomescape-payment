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
import roomescape.auth.annotation.RequiredAdmin;
import roomescape.reservation.service.ReservationTimeService;
import roomescape.reservation.service.dto.request.ReservationTimeRequest;
import roomescape.reservation.service.dto.response.ReservationTimeResponse;

import java.net.URI;
import java.util.List;

@Tag(name = "예약 시간 관리")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "전체 예약 시간 조회", description = "등록되어 있는 모든 예약 시간을 조회한다.")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> readAllReservationTimes() {
        List<ReservationTimeResponse> responses = reservationTimeService.getAll();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 시간 생성", description = "어드민 권한으로 예약 가능 시간을 추가한다.")
    @RequiredAdmin
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> create(@Valid @RequestBody final ReservationTimeRequest request) {
        ReservationTimeResponse response = reservationTimeService.create(request);

        return ResponseEntity.created(URI.create("/times/" + response.id()))
                .body(response);
    }

    @Operation(summary = "예약 시간 삭제", description = "어드민 권한으로 예약 가능 시간을 삭제한다.")
    @RequiredAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        reservationTimeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
