package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationSearchRequestParameter;
import roomescape.reservation.facade.ReservationFacadeService;

import java.util.List;

@Tag(name = "관리자 예약 컨트롤러")
@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationFacadeService reservationFacadeService;

    public AdminReservationController(ReservationFacadeService reservationFacadeService) {
        this.reservationFacadeService = reservationFacadeService;
    }

    @Operation(summary = "예약 생성")
    @PostMapping
    public MemberReservationResponse createReservation(@Valid @RequestBody ReservationCreateRequest request) {
        return reservationFacadeService.createReservation(request);
    }

    @Operation(summary = "예약 목록 조회")
    @GetMapping
    public List<MemberReservationResponse> readReservations() {
        return reservationFacadeService.readReservations();
    }

    @Operation(summary = "예약 목록 검색")
    @GetMapping("/search")
    public List<MemberReservationResponse> searchReservations(ReservationSearchRequestParameter searchCondition) {
        return reservationFacadeService.searchReservations(searchCondition);
    }

    @Operation(summary = "예약 대기 목록 조회")
    @GetMapping("/waiting")
    public List<MemberReservationResponse> readWaitingReservations() {
        return reservationFacadeService.readWaitingReservations();
    }

    @Operation(summary = "예약 대기 승인")
    @PutMapping("/waiting/{id}")
    public void confirmWaitingReservation(@Parameter(description = "MemberReservation id") @PathVariable Long id) {
        reservationFacadeService.confirmWaitingReservation(id);
    }

    @Operation(summary = "예약 대기 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@Parameter(description = "MemberReservation id") @PathVariable Long id) {
        reservationFacadeService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
