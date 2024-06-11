package roomescape.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.LoginMemberId;
import roomescape.config.swagger.ApiErrorResponse;
import roomescape.config.swagger.ApiSuccessResponse;
import roomescape.config.swagger.SwaggerAuthToken;
import roomescape.service.reservation.ReservationWaitingService;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationWaitingResponse;

@Tag(name = "Waiting", description = "예약 대기 컨트롤러입니다.")
@RestController
@RequestMapping("/reservations/waiting")
public class ReservationWaitingController {
    private final ReservationWaitingService reservationWaitingService;

    public ReservationWaitingController(ReservationWaitingService reservationWaitingService) {
        this.reservationWaitingService = reservationWaitingService;
    }

    @PostMapping
    @ApiSuccessResponse.Created("예약 대기 등록")
    @ApiErrorResponse.BadRequest
    @ApiErrorResponse.ThirdPartyApiError
    public ResponseEntity<ReservationWaitingResponse> createReservationWaiting(
            @RequestBody @Valid ReservationRequest waitingRequest,
            @LoginMemberId @SwaggerAuthToken long memberId
    ) {
        ReservationWaitingResponse response = reservationWaitingService.create(waitingRequest, memberId);
        return ResponseEntity.created(URI.create("/reservations/waiting/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{id}")
    @ApiSuccessResponse.NoContent("id를 통해 예약 대기 등록 취소")
    @ApiErrorResponse.ThirdPartyApiError
    public ResponseEntity<Void> deleteReservationWaiting(
            @PathVariable("id") long waitingId,
            @LoginMemberId @SwaggerAuthToken long memberId
    ) {
        reservationWaitingService.deleteById(waitingId, memberId);
        return ResponseEntity.noContent().build();
    }
}
