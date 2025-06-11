package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.AuthenticationPrincipal;
import roomescape.global.auth.annotation.RoleRequired;
import roomescape.global.auth.dto.LoginMember;
import roomescape.member.entity.RoleType;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.response.ReservationAndWaitingResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
@Tag(name = "예약", description = "예약 관련 API")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "예약 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @PostMapping
    @RoleRequired(roleType = {RoleType.ADMIN, RoleType.USER})
    public ResponseEntity<ReservationResponse> createReservation(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginMember loginMember,
            @RequestBody @Valid ReservationCreateRequest request
    ) {
        ReservationResponse response = reservationService.createReservation(loginMember.id(), request);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "나의 예약 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @GetMapping("/mine")
    @RoleRequired(roleType = {RoleType.ADMIN, RoleType.USER})
    public ResponseEntity<List<ReservationAndWaitingResponse>> getMyReservations(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginMember loginMember
    ) {
        List<ReservationAndWaitingResponse> responses = reservationService.getReservationsByMember(loginMember.id());
        return ResponseEntity.ok(responses);
    }
}
