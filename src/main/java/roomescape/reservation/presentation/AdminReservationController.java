package roomescape.reservation.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.Authenticated;
import roomescape.auth.dto.Accessor;
import roomescape.reservation.domain.Status;
import roomescape.reservation.dto.AdminReservationAddRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RestController
@Tag(name = "AdminReservation API", description = "관리자 전용 예약 관련 API")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "관리자용 전체 예약 조회 API", description = "관리자가 전체 요청을 조회합니다.")
    @GetMapping(value = "/admin/reservations", params = {"status"})
    public ResponseEntity<List<ReservationResponse>> findAllWaitingReservation(
            @RequestParam(name = "status") Status status) {
        return ResponseEntity.ok(reservationService.findAllWaitingReservation(status));
    }

    @Operation(summary = "관리자용 예약 생성 API", description = "관리자가 예약을 직접 추가합니다.")
    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(
            @Authenticated Accessor accessor,
            @Valid @RequestBody AdminReservationAddRequest adminReservationAddRequest) {
        ReservationResponse saveResponse = reservationService.saveAdminReservation(
                adminReservationAddRequest.memberId(),
                adminReservationAddRequest.toMemberRequest());
        URI createdUri = URI.create("/reservations/" + saveResponse.id());
        return ResponseEntity.created(createdUri).body(saveResponse);
    }
}
