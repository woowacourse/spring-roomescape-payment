package roomescape.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.dto.CreateRegistrationCommand;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{reservationId}")
    public ReservationResponse getReservationById(
            @PathVariable(value = "reservationId") Long reservationId,
            LoginMember loginMember) {
        return reservationService.getById(reservationId, loginMember);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse saveReservation(
            @Valid @RequestBody final ReservationRequest request,
            final LoginMember member
    ) {
        return reservationService.registerReservation(
                new CreateRegistrationCommand(member.id(), request.date(), request.timeId(), request.themeId())
        );
    }
}
