package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.dto.request.CreateReservationByAdminRequest;
import roomescape.reservation.dto.response.CreateReservationResponse;
import roomescape.reservation.dto.response.FindAdminReservationResponse;
import roomescape.reservation.service.ReservationService;

@Tag(name = "관리자 예약 API", description = "관리자 예약 관련 API")
@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {
    private final ReservationService reservationService;

    public AdminReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "관리자 예약 생성 API")
    @PostMapping
    public ResponseEntity<CreateReservationResponse> createReservationByAdmin(
            @Valid @RequestBody CreateReservationByAdminRequest createReservationByAdminRequest) {
        CreateReservationResponse createReservationResponse = reservationService.createReservationByAdmin(
                createReservationByAdminRequest);
        return ResponseEntity.created(URI.create("/reservations/" + createReservationResponse.id())).body(createReservationResponse);
    }

    @Operation(summary = "관리자 예약 조회 API")
    @GetMapping
    public ResponseEntity<List<FindAdminReservationResponse>> getReservationsByAdmin() {
        return ResponseEntity.ok(reservationService.getReservations());
    }
}
