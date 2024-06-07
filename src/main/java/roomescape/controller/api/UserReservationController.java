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

import roomescape.controller.dto.CreateReservationResponse;
import roomescape.controller.dto.CreateUserReservationRequest;
import roomescape.controller.dto.CreateUserReservationStandbyRequest;
import roomescape.controller.dto.FindMyReservationResponse;
import roomescape.domain.member.Member;
import roomescape.global.argumentresolver.AuthenticationPrincipal;
import roomescape.service.UserReservationService;
import roomescape.service.facade.UserReservationGeneralService;

@RestController
@RequestMapping("/reservations")
public class UserReservationController {

    private final UserReservationGeneralService reservationGeneralService;
    private final UserReservationService reservationService;

    public UserReservationController(UserReservationGeneralService reservationGeneralService, UserReservationService reservationService) {
        this.reservationGeneralService = reservationGeneralService;
        this.reservationService = reservationService;
    }


    @PostMapping
    public ResponseEntity<CreateReservationResponse> save(
            @Valid @RequestBody CreateUserReservationRequest request,
            @AuthenticationPrincipal Member member) {

        CreateReservationResponse response = reservationGeneralService.reserve(member.getId(), request);

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @PostMapping("/standby")
    public ResponseEntity<CreateReservationResponse> standby(
            @Valid @RequestBody CreateUserReservationStandbyRequest request,
            @AuthenticationPrincipal Member member) {

        CreateReservationResponse response = reservationService.standby(member.getId(), request);

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/standby/{id}")
    public ResponseEntity<Void> deleteStandby(@PathVariable Long id, @AuthenticationPrincipal Member member) {
        reservationService.deleteStandby(id, member);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    public ResponseEntity<List<FindMyReservationResponse>> findMyReservations(@AuthenticationPrincipal Member member) {
        List<FindMyReservationResponse> response = reservationService.findMyReservationsWithRank(member.getId());
        return ResponseEntity.ok(response);
    }
}