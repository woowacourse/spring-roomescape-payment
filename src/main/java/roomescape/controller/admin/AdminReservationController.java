package roomescape.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.api.AdminReservationApi;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.response.PendingReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.service.reservation.ReservationService;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminReservationController implements AdminReservationApi {

    private final ReservationService reservationService;

    @Override
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> save(@RequestBody @Valid CreateReservationRequest request) {
        ReservationResponse response = reservationService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getAllByFilter(
            @RequestParam(required = false, name = "memberId") Long memberId,
            @RequestParam(required = false, name = "themeId") Long themeId,
            @RequestParam(required = false, name = "dateFrom") LocalDate dateFrom,
            @RequestParam(required = false, name = "dateTo") LocalDate dateTo
    ) {
        List<ReservationResponse> response = reservationService.getAllFiltered(memberId, themeId, dateFrom, dateTo);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @GetMapping("/reservations/pending")
    public ResponseEntity<List<PendingReservationResponse>> getAllPendings() {
        List<PendingReservationResponse> response = reservationService.getAllPendings();
        return ResponseEntity.ok(response);
    }
    
    @Override
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> remove(@PathVariable long reservationId) {
        reservationService.remove(reservationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @DeleteMapping("/reservations/pending/{reservationId}/deny")
    public ResponseEntity<Void> denyPending(@PathVariable long reservationId) {
        reservationService.denyPending(reservationId);
        return ResponseEntity.ok().build();
    }
}
