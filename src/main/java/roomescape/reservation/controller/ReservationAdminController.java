package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.dto.AdminReservationCreateRequest;
import roomescape.reservation.dto.AdminReservationResponse;
import roomescape.reservation.service.ReservationCommandService;
import roomescape.reservation.service.ReservationQueryService;

@RestController
@RequestMapping("/admin/reservations")
public class ReservationAdminController {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    public ReservationAdminController(final ReservationCommandService reservationCommandService,
                                      final ReservationQueryService reservationQueryService) {
        this.reservationCommandService = reservationCommandService;
        this.reservationQueryService = reservationQueryService;
    }

    @PostMapping
    public ResponseEntity<AdminReservationResponse> createByAdmin(
            @RequestBody @Valid final AdminReservationCreateRequest request) {
        final AdminReservationResponse response = reservationCommandService.createReservationByAdmin(request);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AdminReservationResponse>> findAll(
            @RequestParam(value = "memberId") final Long memberId,
            @RequestParam(value = "themeId") final Long themeId,
            @RequestParam(value = "dateFrom") final LocalDate dateFrom,
            @RequestParam(value = "dateTo") final LocalDate dateTo
    ) {
        final List<AdminReservationResponse> responses = reservationQueryService.findFilteredReservationsForAdmin(
                memberId,
                themeId,
                dateFrom,
                dateTo
        );
        return ResponseEntity.ok().body(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final long id) {
        reservationCommandService.cancelReservationById(id);
        return ResponseEntity.noContent().build();
    }
}
