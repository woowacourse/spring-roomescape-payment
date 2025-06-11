package roomescape.presentation.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.ReservedService;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.reservation.reserved.ReservedSearchFilter;
import roomescape.domain.user.User;
import roomescape.presentation.auth.Authenticated;
import roomescape.presentation.request.CreateReservationAdminRequest;
import roomescape.presentation.request.CreateReservationRequest;
import roomescape.presentation.response.ReservedResponse;

@RestController
public class ReservationController {

    private final ReservedService reservedService;

    public ReservationController(final ReservedService reservedService) {
        this.reservedService = reservedService;
    }

    @PostMapping("/reservations")
    @ResponseStatus(CREATED)
    public ReservedResponse createReservationWithUserPrivileges(
            @Authenticated final User user,
            @RequestBody @Valid final CreateReservationRequest request
    ) {
        Reserved reservation = reservedService.saveReservedWithPurchase(
                user.getId(), request.date(), request.timeId(),
                request.themeId(), request.toPaymentInfo()
        );

        return ReservedResponse.fromReservation(reservation);
    }

    @PostMapping("/admin/reservations")
    @ResponseStatus(CREATED)
    public ReservedResponse createReservationWithAdminPrivileges(
            @RequestBody @Valid final CreateReservationAdminRequest request
    ) {
        Reserved reservation = reservedService.saveReservedWithoutPurchase(
                request.userId(), request.date(),
                request.timeId(), request.themeId()
        );

        return ReservedResponse.fromReservation(reservation);
    }

    @GetMapping("/reservations")
    public List<ReservedResponse> findReservations(
            @RequestParam(name = "themeId", required = false) final Long themeId,
            @RequestParam(name = "userId", required = false) final Long userId,
            @RequestParam(name = "dateFrom", required = false) final LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) final LocalDate dateTo
    ) {
        ReservedSearchFilter searchFilter = new ReservedSearchFilter(themeId, userId, dateFrom, dateTo);
        List<Reserved> reservations = reservedService.findReservedByFilter(searchFilter);

        return ReservedResponse.fromReservations(reservations);
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteReservation(@PathVariable("id") final long id) {
        reservedService.removeById(id);
    }
}
