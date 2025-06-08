package roomescape.reservation.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import roomescape.auth.dto.LoginMember;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.reservation.controller.api.ReservationApi;
import roomescape.reservation.dto.request.FilteringReservationRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationPaymentRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.BookedReservationTimeResponse;
import roomescape.reservation.dto.response.MyReservationsResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RequiredArgsConstructor
@RestController
public class ReservationController implements ReservationApi {

    private final ReservationService reservationService;

    @Override
    public ResponseEntity<List<ReservationResponse>> readAllReservations() {
        List<ReservationResponse> response = reservationService.getAll();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<BookedReservationTimeResponse>> readAvailableReservationTimes(
            final LocalDate date,
            final Long themeId
    ) {
        List<BookedReservationTimeResponse> responses = reservationService.getSortedAvailableTimes(date, themeId);

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody final ReservationPaymentRequest request,
            final LoginMember loginMember
    ) {
        ReservationCreateRequest createRequest =
                ReservationCreateRequest.from(
                        new ReservationRequest(request.date(), request.timeId(), request.themeId()),
                        loginMember
                );
        PaymentRequest paymentRequest = new PaymentRequest(request.paymentKey(), request.orderId(), request.amount());
        ReservationResponse response = reservationService.createWithPayment(createRequest, paymentRequest);

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @Override
    public ResponseEntity<Void> delete(final Long reservationId) {
        reservationService.delete(reservationId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ReservationResponse>> findAllByFilter(
            @ModelAttribute @Valid final FilteringReservationRequest request
    ) {
        final List<ReservationResponse> reservationResponses =
                reservationService.findReservationByFiltering(request);

        return ResponseEntity.ok(reservationResponses);
    }

    @Override
    public ResponseEntity<List<MyReservationsResponse>> getMyReservations(final @Valid LoginMember loginMember) {
        List<MyReservationsResponse> response = reservationService.getAllMyReservations(loginMember);
        return ResponseEntity.ok(response);
    }
}
