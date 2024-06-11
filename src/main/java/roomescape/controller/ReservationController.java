package roomescape.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.LoginMemberId;
import roomescape.config.swagger.ApiErrorResponse;
import roomescape.config.swagger.ApiSuccessResponse;
import roomescape.config.swagger.SwaggerAuthToken;
import roomescape.service.reservation.ReservationService;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationResponse;

@Tag(name = "Reservation", description = "예약 컨트롤러입니다.")
@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    @ApiSuccessResponse.Ok("전체 예약 조회")
    public List<ReservationResponse> findAllReservations() {
        return reservationService.findAll();
    }

    @PostMapping
    @ApiSuccessResponse.Created("예약 생성")
    @ApiErrorResponse.BadRequest
    @ApiErrorResponse.ThirdPartyApiError
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid ReservationRequest reservationRequest,
            @LoginMemberId @SwaggerAuthToken long memberId
    ) {
        ReservationResponse reservationResponse = reservationService.create(reservationRequest, memberId);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @DeleteMapping("/{id}")
    @ApiSuccessResponse.NoContent("id를 통해 예약 삭제")
    @ApiErrorResponse.ThirdPartyApiError
    public ResponseEntity<Void> deleteReservation(
            @PathVariable("id") long reservationId,
            @LoginMemberId @SwaggerAuthToken long memberId
    ) {
        reservationService.deleteById(reservationId, memberId);
        return ResponseEntity.noContent().build();
    }
}
