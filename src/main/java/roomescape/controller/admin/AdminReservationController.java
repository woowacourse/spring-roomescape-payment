package roomescape.controller.admin;

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
import roomescape.domain.reservation.Status;
import roomescape.dto.request.reservation.AdminReservationRequest;
import roomescape.dto.request.reservation.ReservationCriteriaRequest;
import roomescape.dto.response.reservation.CanceledReservationWebResponse;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.service.ReservationService;

@Tag(name = "어드민 예약 API", description = "어드민 예약 관련 API 입니다.")
@RestController
@RequestMapping("/admin")
public class AdminReservationController {
    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "어드민 예약 대기 조회 API")
    @GetMapping("/waitings")
    public ResponseEntity<List<ReservationResponse>> findAllByWaiting() {
        List<ReservationResponse> responses = reservationService.findAllByStatus(Status.WAITING);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "어드민 예약 추가 API")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByAdmin(@RequestBody @Valid AdminReservationRequest adminReservationRequest) {
        ReservationResponse reservationResponse = reservationService.reserveReservationByAdmin(adminReservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "어드민 예약 검색 API")
    @GetMapping("/reservations/search")
    public ResponseEntity<List<ReservationResponse>> searchAdmin(ReservationCriteriaRequest reservationCriteriaRequest) {
        List<ReservationResponse> responses = reservationService.findByCriteria(reservationCriteriaRequest);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "어드민 취소 예약 조회 API")
    @GetMapping("/reservations/canceled")
    public ResponseEntity<List<CanceledReservationWebResponse>> findAllCanceledReservation() {
        List<CanceledReservationWebResponse> canceledReservationResponses = reservationService.findAllCanceledReservation();
        return ResponseEntity.ok(canceledReservationResponses);
    }
}
