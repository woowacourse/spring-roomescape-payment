package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import roomescape.annotation.CheckRole;
import roomescape.controller.apidocs.ReservationController;
import roomescape.dto.request.ConfirmWaitReservationRequest;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.request.CreateWaitReservationRequest;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.response.MyReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationWaitResponse;
import roomescape.global.Role;
import roomescape.service.ReservationFacadeService;

@RestController
@RequestMapping("/reservations")
public class ReservationControllerImpl implements ReservationController {

    private final ReservationFacadeService reservationFacadeService;

    public ReservationControllerImpl(ReservationFacadeService reservationFacadeService) {
        this.reservationFacadeService = reservationFacadeService;
    }

    @GetMapping
    @CheckRole(Role.ADMIN)
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        List<ReservationResponse> responses = reservationFacadeService.findAllReservation();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/mine")
    @CheckRole({Role.USER, Role.ADMIN})
    public ResponseEntity<List<MyReservationResponse>> getMyReservation(LoginMemberRequest loginMemberRequest) {
        List<MyReservationResponse> responses = reservationFacadeService.findAllReservationOfMember(loginMemberRequest);

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @CheckRole(value = {Role.ADMIN, Role.USER})
    public ResponseEntity<ReservationResponse> addReservations(
            @RequestBody @Valid CreateReservationRequest request,
            LoginMemberRequest loginMemberRequest) {

        ReservationResponse response = reservationFacadeService.addReservation(request, loginMemberRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/waiting")
    @CheckRole(value = {Role.ADMIN, Role.USER})
    public ResponseEntity<ReservationWaitResponse> addWaitReservation(
            @RequestBody @Valid CreateWaitReservationRequest request,
            LoginMemberRequest loginMemberRequest) {
        ReservationWaitResponse response = reservationFacadeService.addWaitReservation(request, loginMemberRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/waiting/{reservationId}/confirm")
    @CheckRole(value = {Role.ADMIN, Role.USER})
    public ResponseEntity<ReservationResponse> confirmWaitReservation(@PathVariable("reservationId") Long reservationId,
                                                                      @Valid @RequestBody ConfirmWaitReservationRequest request,
                                                                      LoginMemberRequest loginMemberRequest) {
        ReservationResponse response = reservationFacadeService.pendingToReserve(reservationId, request,
                loginMemberRequest);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @CheckRole(value = {Role.ADMIN, Role.USER})
    public ResponseEntity<Void> deleteReservations(@PathVariable Long id) {
        reservationFacadeService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/waiting/{id}")
    @CheckRole(value = {Role.ADMIN, Role.USER})
    public ResponseEntity<Void> deleteWaitReservation(@PathVariable Long id) {
        reservationFacadeService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }
}
