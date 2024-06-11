package roomescape.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.swagger.ApiErrorResponse;
import roomescape.config.swagger.ApiSuccessResponse;
import roomescape.service.reservation.ReservationService;
import roomescape.service.reservation.ReservationWaitingService;
import roomescape.service.reservation.dto.AdminReservationRequest;
import roomescape.service.reservation.dto.ReservationFilterRequest;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.reservation.dto.ReservationWaitingResponse;

@Tag(name = "Reservation (Admin)", description = "관리자 예약 API 입니다.")
@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {
    private final ReservationService reservationService;
    private final ReservationWaitingService reservationWaitingService;

    public AdminReservationController(ReservationService reservationService,
                                      ReservationWaitingService reservationWaitingService) {
        this.reservationService = reservationService;
        this.reservationWaitingService = reservationWaitingService;
    }

    @PostMapping
    @ApiSuccessResponse.Created("관리자에 의한 예약 생성")
    @ApiErrorResponse.BadRequest
    @ApiErrorResponse.Forbidden
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid AdminReservationRequest adminReservationRequest) {
        ReservationResponse reservationResponse = reservationService.create(
                ReservationRequest.fromAdminRequest(adminReservationRequest), adminReservationRequest.memberId()
        );
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @DeleteMapping("/{id}")
    @ApiSuccessResponse.NoContent("관리자에 의한 예약 취소")
    @ApiErrorResponse.Forbidden
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @ApiSuccessResponse.Ok("멤버별, 테마별, 방문 일자 기간별 예약 조회")
    @ApiErrorResponse.Forbidden
    public List<ReservationResponse> findReservations(
            @ModelAttribute("ReservationFindRequest") ReservationFilterRequest reservationFilterRequest) {
        return reservationService.findByCondition(reservationFilterRequest);
    }

    @GetMapping("/waiting")
    @ApiSuccessResponse.Ok("모든 예약 대기 조회")
    @ApiErrorResponse.Forbidden
    public ResponseEntity<List<ReservationWaitingResponse>> findReservationWaitings() {
        List<ReservationWaitingResponse> totalWaiting = reservationWaitingService.findAll();
        return ResponseEntity.ok().body(totalWaiting);
    }

    @DeleteMapping("/waiting/{id}")
    @ApiSuccessResponse.NoContent("관리자에 의한 예약 대기 거절")
    @ApiErrorResponse.Forbidden
    public ResponseEntity<Void> refuseReservationWaiting(@PathVariable("id") long waitingId) {
        reservationWaitingService.deleteById(waitingId);
        return ResponseEntity.noContent().build();
    }
}
