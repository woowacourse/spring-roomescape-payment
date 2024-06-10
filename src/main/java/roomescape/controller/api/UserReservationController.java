package roomescape.controller.api;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.request.CreateUserReservationRequest;
import roomescape.controller.dto.request.CreateUserReservationStandbyRequest;
import roomescape.controller.dto.response.MyReservationResponse;
import roomescape.controller.dto.response.ReservationResponse;
import roomescape.domain.member.Member;
import roomescape.global.argumentresolver.AuthenticationPrincipal;
import roomescape.service.UserReservationService;
import roomescape.service.facade.UserReservationGeneralService;

@Tag(name = "UserReservation", description = "사용자 예약 관련 API")
@RestController
@RequestMapping("/reservations")
public class UserReservationController {
    private final UserReservationGeneralService reservationGeneralService;
    private final UserReservationService reservationService;

    public UserReservationController(UserReservationGeneralService reservationGeneralService, UserReservationService reservationService) {
        this.reservationGeneralService = reservationGeneralService;
        this.reservationService = reservationService;
    }

    @Operation(summary = "내 예약/예약 대기 조회", description = "내 예약/예약 대기를 모두 조회할 수 있다.")
    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationResponse>> findMyReservations(@AuthenticationPrincipal Member member) {
        List<MyReservationResponse> response = reservationService.findMyReservationsWithRank(member.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예약 생성", description = "예약을 생성할 수 있다.")
    @PostMapping
    public ResponseEntity<ReservationResponse> save(
            @Valid @RequestBody CreateUserReservationRequest request,
            @AuthenticationPrincipal Member member) {

        ReservationResponse response = reservationGeneralService.reserve(member.getId(), request);

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @Operation(summary = "예약 대기 생성", description = "예약 대기를 생성할 수 있다.")
    @PostMapping("/standby")
    public ResponseEntity<ReservationResponse> standby(
            @Valid @RequestBody CreateUserReservationStandbyRequest request,
            @AuthenticationPrincipal Member member) {

        ReservationResponse response = reservationService.standby(member.getId(), request);

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @Operation(summary = "예약 대기 삭제", description = "예약을 대기를 삭제할 수 있다.")
    @DeleteMapping("/standby/{id}")
    public ResponseEntity<Void> deleteStandby(@PathVariable Long id, @AuthenticationPrincipal Member member) {
        reservationService.deleteStandby(id, member);
        return ResponseEntity.noContent().build();
    }
}
