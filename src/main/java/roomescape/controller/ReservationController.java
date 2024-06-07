package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.Login;
import roomescape.controller.dto.UserReservationSaveRequest;
import roomescape.controller.dto.UserReservationViewResponse;
import roomescape.controller.dto.UserReservationViewResponses;
import roomescape.service.ReservationService;
import roomescape.service.dto.request.LoginMember;
import roomescape.service.dto.response.ReservationResponse;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "사용자 예약 추가 API", description = "사용자의 예약을 추가한다.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(
            @Login LoginMember member,
            @RequestBody @Valid UserReservationSaveRequest userReservationSaveRequest
    ) {
        ReservationResponse reservationResponse = reservationService.saveMemberReservation(member,
                userReservationSaveRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "사용자 예약 조회 API", description = "사용자의 예약을 조회한다.")
    @GetMapping("/reservations-mine")
    public ResponseEntity<UserReservationViewResponses> findAllUserReservation(@Login LoginMember member) {
        List<UserReservationViewResponse> reservationResponses = reservationService.findAllUserReservation(member.id())
                .stream()
                .map(UserReservationViewResponse::from)
                .toList();
        UserReservationViewResponses response = new UserReservationViewResponses(reservationResponses);
        return ResponseEntity.ok(response);
    }
}
