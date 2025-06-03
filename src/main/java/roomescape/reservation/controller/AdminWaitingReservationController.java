package roomescape.reservation.controller;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.WaitingReservationService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/waitings")
public class AdminWaitingReservationController {

    private final WaitingReservationService waitingReservationService;

    @GetMapping()
    public List<ReservationResponse> getWaitingReservations() {
        return waitingReservationService.getAll();
    }

    @PatchMapping("/{id}")
    public void approveWaitingReservation(@PathVariable @NotNull final Long id) {
        waitingReservationService.approveWaitingReservation(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWaitingReservation(@PathVariable @NotNull final Long id) {
        waitingReservationService.denyWaitingByIdForAdmin(id);
    }
}
