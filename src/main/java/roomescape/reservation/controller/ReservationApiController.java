package roomescape.reservation.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import roomescape.auth.Login;
import roomescape.member.dto.LoginMemberInToken;
import roomescape.reservation.dto.request.FreeReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationSearchRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.WaitingResponse;
import roomescape.reservation.service.PaymentService;
import roomescape.reservation.service.ReservationService;

@RestController
public class ReservationApiController {
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationApiController(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> findAll() {
        List<ReservationResponse> responses = reservationService.findAll();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/reservations/search")
    public ResponseEntity<List<ReservationResponse>> findAllBySearchCond(
            @Valid @ModelAttribute ReservationSearchRequest request
    ) {
        List<ReservationResponse> responses = reservationService.findAllBySearch(request);

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createMemberReservation(
            @Valid @RequestBody ReservationCreateRequest request,
            @Login LoginMemberInToken loginMemberInToken
    ) {
        ReservationResponse response = paymentService.purchase(request, loginMemberInToken);

        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> createAdminReservation(
            @Valid @RequestBody FreeReservationCreateRequest request,
            @Login LoginMemberInToken loginMemberInToken
    ) {
        ReservationResponse response = reservationService.save(request, loginMemberInToken);

        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @GetMapping("/reservations/waiting")
    public ResponseEntity<List<WaitingResponse>> findWaiting() {
        List<WaitingResponse> responses = reservationService.findWaiting();

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        reservationService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations/me")
    public ResponseEntity<List<MyReservationResponse>> myReservations(@Login LoginMemberInToken loginMemberInToken) {
        List<MyReservationResponse> responses = reservationService.findChargedReservationByMemberId(
                loginMemberInToken.id());
        responses.addAll(reservationService.findFreeReservationByMemberId(loginMemberInToken.id()));

        return ResponseEntity.ok(responses);
    }
}
