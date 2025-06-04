package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.dto.AdminReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.dto.CreateRegistrationCommand;


@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse saveReservation(@Valid @RequestBody final AdminReservationRequest request) {
        return reservationService.registerReservation(
                new CreateRegistrationCommand(request.memberId(), request.date(), request.timeId(), request.themeId())
        );
    }

    @GetMapping
    public List<ReservationResponse> findReservationsByCriteria(
            @ModelAttribute final ReservationSearchRequest request) {
        return reservationService.findReservationsByCriteria(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable final Long id) {
        reservationService.deleteById(id);
    }
}
