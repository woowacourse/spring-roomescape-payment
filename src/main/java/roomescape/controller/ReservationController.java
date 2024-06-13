package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "[USER] 예약 API", description = "사용자가 예약을 생성/조회할 수 있습니다.")
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "사용자 예약 추가 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "생성된 예약 정보를 반환합니다.")
    })
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

    @Operation(summary = "사용자 예약 조회 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "사용자 예약 정보를 반환합니다.")
    })
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
