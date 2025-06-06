package roomescape.reservation.ui;

import static roomescape.auth.domain.AuthRole.ADMIN;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.domain.RequiresRole;
import roomescape.reservation.application.ReservationTimeService;
import roomescape.reservation.ui.dto.request.CreateReservationTimeRequest;
import roomescape.reservation.ui.dto.response.ReservationTimeResponse;

@Slf4j
@Tag(name = "예약 시간", description = "예약 시간에 관련 api")
@RestController
@RequestMapping("/times")
@RequiredArgsConstructor
public class ReservationTimeRestController {

    private final ReservationTimeService reservationTimeService;

    @Operation(summary = "예약 시간 생성", description = "예약 시간을 생성합니다.")
    @PostMapping
    @RequiresRole(authRoles = {ADMIN})
    public ResponseEntity<ReservationTimeResponse> create(
            @RequestBody @Valid final CreateReservationTimeRequest request
    ) {
        log.info("예약 시간 생성 요청 수신");

        final ReservationTimeResponse response = reservationTimeService.create(request);

        log.info("예약 시간 생성 완료 - 시간 ID: {}", response.id());

        return ResponseEntity.created(URI.create("/times/" + response.id()))
                .body(response);
    }

    @Operation(summary = "예약 시간 삭제", description = "예약 시간을 삭제합니다.")
    @DeleteMapping("/{id}")
    @RequiresRole(authRoles = {ADMIN})
    public ResponseEntity<Void> delete(
            @PathVariable final Long id
    ) {
        log.info("예약 시간 삭제 요청 - 시간 ID: {}", id);

        reservationTimeService.deleteById(id);

        log.info("예약 시간 삭제 완료 - 시간 ID: {}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모든 예약 시간 조회", description = "모든 예약 시간을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> findAll() {
        log.info("예약 시간 전체 조회 요청 수신");

        List<ReservationTimeResponse> result = reservationTimeService.findAll();

        log.info("예약 시간 전체 조회 완료 - 개수: {}", result.size());

        return ResponseEntity.ok(result);
    }
}
