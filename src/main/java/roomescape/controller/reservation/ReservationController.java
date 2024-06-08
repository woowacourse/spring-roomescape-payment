package roomescape.controller.reservation;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import roomescape.controller.member.dto.LoginMember;
import roomescape.controller.reservation.dto.CreateReservationRequest;
import roomescape.controller.reservation.dto.ReservationResponse;
import roomescape.controller.reservation.dto.UserCreateReservationRequest;
import roomescape.controller.time.dto.IsMineRequest;
import roomescape.domain.Reservation;
import roomescape.repository.dto.ReservationRankResponse;
import roomescape.service.ReservationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationResponse> getReservations() {
        return reservationService.getReservations()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/mine")
    public List<ReservationRankResponse> getMineReservation(final LoginMember member) {
        return reservationService.getMyReservation(member);
    }

    @GetMapping(value = "/is-mine", params = {"themeId", "timeId", "date"})
    public boolean isMine(@Valid final IsMineRequest request, LoginMember member) {
        return reservationService.isMyReservation(request, member);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> addReservation(
            @RequestBody @Valid final UserCreateReservationRequest request,
            @Valid final LoginMember loginMember) {

        final CreateReservationRequest create = CreateReservationRequest.from(request, loginMember);

        final Reservation reservation = reservationService.addUserReservation(create);
        final URI uri = UriComponentsBuilder.fromPath("/reservations/{id}")
                .buildAndExpand(reservation.getId())
                .toUri();

        return ResponseEntity.created(uri)
                .body(ReservationResponse.from(reservation));
    }

    @DeleteMapping("/wait/{id}")
    public ResponseEntity<Void> deleteWaitReservation(@PathVariable("id") final long id, final LoginMember member) {
        reservationService.deleteWaitReservation(id, member.id());
        return ResponseEntity.noContent()
                .build();
    }
}
