package roomescape.reservation.ui;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.application.ReservationCommandService;
import roomescape.reservation.application.ReservationQueryService;
import roomescape.reservation.application.dto.AdminReservationRequest;
import roomescape.reservation.application.dto.ReservationResponse;
import roomescape.reservation.application.dto.WaitingResponse;

@RestController
@RequestMapping("/admin")
public class AdminReservationController {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    public AdminReservationController(
            final ReservationCommandService reservationCommandService,
            final ReservationQueryService reservationQueryService
    ) {
        this.reservationCommandService = reservationCommandService;
        this.reservationQueryService = reservationQueryService;
    }

    @Operation(summary = "관리자 모든 예약 조회 API")
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> findAllReserved() {
        return ResponseEntity.ok(reservationQueryService.findReservedReservations());
    }

    @Operation(summary = "관리자 모든 대기 조회 API")
    @GetMapping("/waitings")
    public ResponseEntity<List<WaitingResponse>> findAllWaiting() {
        return ResponseEntity.ok(reservationQueryService.findWaitingReservations());
    }

    @Operation(summary = "관리자 예약 조건 조회 API")
    @GetMapping("/reservations/search")
    public ResponseEntity<List<ReservationResponse>> getFilteredReservations(
            @RequestParam(required = false, name = "themeId") final Long themeId,
            @RequestParam(required = false, name = "memberId") final Long memberId,
            @RequestParam(required = false, name = "from") final LocalDate start,
            @RequestParam(required = false, name = "to") final LocalDate end
    ) {
        final List<ReservationResponse> reservationResponses = reservationQueryService.findReservationByThemeIdAndMemberIdInDuration(
                themeId, memberId, start, end);
        return ResponseEntity.ok(reservationResponses);
    }

    @Operation(summary = "관리자 예약 추가 API")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> add(
            @Valid @RequestBody final AdminReservationRequest request
    ) {
        final ReservationResponse response = reservationCommandService.addAdminReservation(request);
        return ResponseEntity.created(URI.create("/admin/reservations/" + response.id()))
                .body(response);
    }

    @Operation(summary = "관리자 예약 제거 API")
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") final Long id) {
        reservationCommandService.deleteReservationById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "관리자 대기 거절 API")
    @PutMapping("/waitings/reject/{id}")
    public ResponseEntity<Void> rejectWaiting(@PathVariable("id") final Long id) {
        reservationCommandService.rejectWaitingById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관리자 대기 승인 API")
    @PutMapping("/waitings/accept/{id}")
    public ResponseEntity<Void> acceptReservation(
            @PathVariable("id") final Long id
    ) {
        reservationCommandService.acceptReservation(id);
        return ResponseEntity.ok().build();
    }
}
