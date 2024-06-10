package roomescape.web.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.reservation.ReservationFilter;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.service.reservation.ReservationRegisterService;
import roomescape.service.reservation.ReservationSearchService;
import roomescape.service.reservation.WaitingApproveService;

@Tag(name = "관리자 예약 관리")
@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationRegisterService registerService;
    private final ReservationSearchService searchService;
    private final WaitingApproveService waitingService;

    public AdminReservationController(ReservationRegisterService registerService,
                                      ReservationSearchService searchService,
                                      WaitingApproveService waitingService
    ) {
        this.registerService = registerService;
        this.searchService = searchService;
        this.waitingService = waitingService;
    }

    @Operation(summary = "관리자 예약 등록", description = "관리자가 회원의 예약을 등록한다.")
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        ReservationResponse response = registerService.registerReservationByAdmin(request);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @Operation(summary = "조건 예약 조회", description = "특정한 조건으로 등록된 예약을 조회한다.")
    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponse>> getReservationsByFilter(
            @ModelAttribute ReservationFilter reservationFilter) {
        List<ReservationResponse> responses = searchService.findReservationsByFilter(reservationFilter);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "모든 예약 대기 조회", description = "등록된 모든 대기 상태의 예약을 조회한다.")
    @GetMapping("/waiting")
    public ResponseEntity<List<ReservationResponse>> getAllWaitingReservations() {
        List<ReservationResponse> responses = searchService.findAllWaitingReservations();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 대기 승인", description = "대기 상태의 예약을 승인한다.")
    @PatchMapping("/waiting/approve/{id}")
    public ResponseEntity<Void> approveWaitingReservation(@PathVariable Long id) {
        waitingService.approveWaitingReservation(id);
        return ResponseEntity.ok().build();
    }
}
