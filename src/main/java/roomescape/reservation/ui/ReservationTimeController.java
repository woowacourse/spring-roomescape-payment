package roomescape.reservation.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.application.ReservationTimeService;
import roomescape.reservation.application.dto.ReservationTimeRequest;
import roomescape.reservation.application.dto.ReservationTimeResponse;

@Tag(name = "예약 시간", description = "예약 시간 관련 API")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "모든 예약 시간 조회 API", description = "등록된 모든 예약 시간을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> findAll() {
        return ResponseEntity.ok(reservationTimeService.findAll());
    }

    @Operation(summary = "예약 시간 추가 API", description = "새로운 예약 시간을 추가합니다. 요청 본문에 예약 시간 정보를 포함해야 합니다.")
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> add(@Valid @RequestBody final ReservationTimeRequest requestDto) {
        return new ResponseEntity<>(reservationTimeService.add(requestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "예약 시간 삭제 API", description = "예약 시간을 ID로 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final Long id) {
        reservationTimeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
