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
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.LoginMemberConverter;
import roomescape.controller.swagger.SwaggerAuthHeader;
import roomescape.controller.swagger.SwaggerBadRequestError;
import roomescape.controller.swagger.SwaggerCreated;
import roomescape.controller.swagger.SwaggerNoContent;
import roomescape.controller.swagger.SwaggerNotFound;
import roomescape.controller.swagger.SwaggerOk;
import roomescape.dto.LoginMember;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.request.reservation.WaitingRequest;
import roomescape.dto.response.reservation.MyReservationResponse;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.service.ReservationService;
import roomescape.service.ReservationWaitingService;

@Tag(name = "예약", description = "예약 및 예약 대기 API입니다.")
@RestController
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationWaitingService reservationWaitingService;

    public ReservationController(ReservationService reservationService,
                                 ReservationWaitingService reservationWaitingService) {
        this.reservationService = reservationService;
        this.reservationWaitingService = reservationWaitingService;
    }

    @SwaggerOk(summary = "전체 예약 조회")
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> findAllReservations() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @SwaggerCreated(summary = "예약 생성(회원)", description = "회원에 의한 예약 생성 API")
    @SwaggerBadRequestError
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByClient(
            @SwaggerAuthHeader @LoginMemberConverter LoginMember loginMember,
            @RequestBody @Valid ReservationRequest reservationRequest) {
        ReservationResponse response = reservationService.saveReservationByClient(loginMember, reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @SwaggerCreated(summary = "예약 대기 생성")
    @SwaggerBadRequestError
    @PostMapping("/waitings")
    public ResponseEntity<ReservationResponse> saveWaitingByClient(
            @SwaggerAuthHeader @LoginMemberConverter LoginMember loginMember,
            @RequestBody @Valid WaitingRequest waitingRequest
    ) {
        ReservationResponse response = reservationWaitingService.saveReservationWaiting(waitingRequest, loginMember);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @SwaggerNoContent(summary = "예약 삭제")
    @SwaggerNotFound
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable long reservationId) {
        reservationService.cancelById(reservationId);
        return ResponseEntity.noContent().build();
    }

    @SwaggerNoContent(summary = "예약 대기 삭제")
    @SwaggerNotFound
    @DeleteMapping("/waitings/{reservationWaitingId}")
    public ResponseEntity<Void> deleteReservationWaiting(@PathVariable long reservationWaitingId) {
        reservationWaitingService.deleteById(reservationWaitingId);
        return ResponseEntity.noContent().build();
    }

    @SwaggerOk(summary = "로그인 회원 예약 및 예약 대기 조회")
    @GetMapping("/reservations/mine")
    public ResponseEntity<List<MyReservationResponse>> findMyReservations(
            @SwaggerAuthHeader @LoginMemberConverter LoginMember loginMember) {
        List<MyReservationResponse> responses = reservationService.findMyReservations(loginMember.id());
        return ResponseEntity.ok(responses);
    }
}
