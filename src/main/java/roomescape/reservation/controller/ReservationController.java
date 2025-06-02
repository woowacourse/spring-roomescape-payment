package roomescape.reservation.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.service.dto.LoginMember;
import roomescape.reservation.service.CreateReservationWithPaymentService;
import roomescape.reservation.service.DeleteReservationService;
import roomescape.reservation.service.ReservationQueryService;
import roomescape.reservation.service.dto.request.FilteringReservationRequest;
import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.service.dto.response.MyReservationsResponse;
import roomescape.reservation.service.dto.response.ReservationResponse;
import roomescape.reservation.service.dto.response.ReservationTimeWithBookedResponse;
import roomescape.reservation.service.dto.response.ReservationWithPaymentResponse;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/reservations")
@RestController
public class ReservationController {

    private final ReservationQueryService reservationQueryService;
    private final CreateReservationWithPaymentService createReservationWithPaymentService;
    private final DeleteReservationService deleteReservationService;

    public ReservationController(
            final ReservationQueryService reservationQueryService,
            final CreateReservationWithPaymentService createReservationWithPaymentService,
            final DeleteReservationService deleteReservationService
    ) {
        this.reservationQueryService = reservationQueryService;
        this.createReservationWithPaymentService = createReservationWithPaymentService;
        this.deleteReservationService = deleteReservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> readAllReservations() {
        List<ReservationResponse> response = reservationQueryService.getAll();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/times")
    public ResponseEntity<List<ReservationTimeWithBookedResponse>> readAvailableReservationTimes(
            @RequestParam("date") final LocalDate date,
            @RequestParam("themeId") final Long themeId
    ) {
        List<ReservationTimeWithBookedResponse> responses = reservationQueryService.getReservationTimesWithBooked(date, themeId);

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createWithPayment(
            @Valid @RequestBody ReservationWithPaymentRequest request,
            final LoginMember loginMember
    ) {
        ReservationWithPaymentResponse response = createReservationWithPaymentService.create(request, loginMember);

        return ResponseEntity.ok(ReservationResponse.from(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final Long id, LoginMember loginMember) {
        deleteReservationService.delete(id, loginMember);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtering")
    public ResponseEntity<List<ReservationResponse>> findAllByFilter(
            @ModelAttribute @Valid final FilteringReservationRequest request
    ) {
        final List<ReservationResponse> reservationResponses = reservationQueryService.findReservationByFiltering(request);

        return ResponseEntity.ok(reservationResponses);
    }

    @GetMapping("/my")
    public ResponseEntity<List<MyReservationsResponse>> getMyReservations(@Valid LoginMember loginMember) {
        List<MyReservationsResponse> response = reservationQueryService.getAllLoginMemberReservations(loginMember);
        return ResponseEntity.ok(response);
    }
}
