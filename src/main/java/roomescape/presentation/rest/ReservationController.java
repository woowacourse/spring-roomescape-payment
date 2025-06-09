package roomescape.presentation.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.PaymentService;
import roomescape.application.ReservationService;
import roomescape.domain.auth.AuthenticationInfo;
import roomescape.domain.reservation.ReservationSearchFilter;
import roomescape.presentation.auth.AdminOnly;
import roomescape.presentation.request.CreateReservationRequest;
import roomescape.presentation.request.ReservationPaymentRequest;
import roomescape.presentation.response.ReservationResponse;

@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(CREATED)
    public ReservationResponse reserve(
        final AuthenticationInfo authenticationInfo,
        @RequestBody @Valid final CreateReservationRequest request
    ) {
        var reservation = reservationService.reserve(authenticationInfo.id(), request.date(), request.timeId(), request.themeId());
        return ReservationResponse.from(reservation);
    }

    @PostMapping("/{id}/payment")
    @ResponseStatus(HttpStatus.OK)
    public void confirm(@PathVariable("id") final long id, @RequestBody final ReservationPaymentRequest request) {
        paymentService.confirm(id, request.paymentKey(), request.orderId(), request.amount());
    }

    @PostMapping("/wait")
    @ResponseStatus(CREATED)
    public ReservationResponse waitFor(
        final AuthenticationInfo authenticationInfo,
        @RequestBody @Valid final CreateReservationRequest request
    ) {
        var reservation = reservationService.waitFor(authenticationInfo.id(), request.date(), request.timeId(), request.themeId());
        return ReservationResponse.from(reservation);
    }

    @GetMapping
    public List<ReservationResponse> getAllReservations(
        @RequestParam(name = "themeId", required = false) final Long themeId,
        @RequestParam(name = "userId", required = false) final Long userId,
        @RequestParam(name = "dateFrom", required = false) final LocalDate dateFrom,
        @RequestParam(name = "dateTo", required = false) final LocalDate dateTo
    ) {
        var searchFilter = new ReservationSearchFilter(themeId, userId, dateFrom, dateTo);
        var reservations = reservationService.findAllReservations(searchFilter);
        return ReservationResponse.from(reservations);
    }

    @AdminOnly
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable("id") final long id) {
        reservationService.removeById(id);
    }

    @PostMapping("/cancel/{id}")
    @ResponseStatus(NO_CONTENT)
    public void cancel(
        final AuthenticationInfo authenticationInfo,
        @PathVariable("id") final long reservationId
    ) {
        var userId = authenticationInfo.id();
        reservationService.cancel(userId, reservationId);
    }
}
