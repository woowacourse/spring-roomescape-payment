package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.response.PendingReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.service.reservation.ReservationService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "5. 어드민 전용 API")
@RequiredArgsConstructor
@RestController
public class AdminReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "예약 추가")
    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> save(@RequestBody @Valid CreateReservationRequest request) {
        ReservationResponse response = reservationService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "예약 필터링 조회")
    @GetMapping("/admin/reservations")
    public ResponseEntity<List<ReservationResponse>> getAllByFilter(
            @RequestParam(required = false, name = "memberId") Long memberId,
            @RequestParam(required = false, name = "themeId") Long themeId,
            @RequestParam(required = false, name = "dateFrom") LocalDate dateFrom,
            @RequestParam(required = false, name = "dateTo") LocalDate dateTo
    ) {
        List<ReservationResponse> response = reservationService.getAllFiltered(memberId, themeId, dateFrom, dateTo);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "대기 예약 조회")
    @GetMapping("/admin/reservations/pending")
    public ResponseEntity<List<PendingReservationResponse>> getAllPendings() {
        List<PendingReservationResponse> response = reservationService.getAllPendings();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "대기 예약 거절")
    @DeleteMapping("/admin/reservations/pending/{reservationId}/deny")
    public ResponseEntity<Void> denyPending(@PathVariable long reservationId) {
        reservationService.denyPending(reservationId);
        return ResponseEntity.ok().build();
    }
}
