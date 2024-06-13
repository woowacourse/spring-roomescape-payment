package roomescape.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.request.ReservationRequest;
import roomescape.controller.dto.response.ApiResponses;
import roomescape.controller.support.Auth;
import roomescape.security.authentication.Authentication;
import roomescape.service.ReservationPaymentFacadeService;
import roomescape.service.ReservationQueryService;
import roomescape.service.dto.request.ReservationCancelRequest;
import roomescape.service.dto.request.ReservationCreateRequest;
import roomescape.service.dto.response.PersonalReservationResponse;
import roomescape.service.dto.response.ReservationResponse;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationPaymentFacadeService reservationPaymentFacadeService;
    private final ReservationQueryService reservationQueryService;

    public ReservationController(ReservationPaymentFacadeService reservationPaymentFacadeService,
                                 ReservationQueryService reservationQueryService) {
        this.reservationPaymentFacadeService = reservationPaymentFacadeService;
        this.reservationQueryService = reservationQueryService;
    }

    @GetMapping
    public ApiResponses<ReservationResponse> getReservationsByConditions(@RequestParam(required = false) Long memberId,
                                                                         @RequestParam(required = false) Long themeId,
                                                                         @RequestParam(required = false) LocalDate dateFrom,
                                                                         @RequestParam(required = false) LocalDate dateTo) {
        List<ReservationResponse> responses = reservationQueryService.getReservationsByConditions(memberId, themeId, dateFrom, dateTo);
        return new ApiResponses<>(responses);
    }

    @GetMapping("/mine")
    public ApiResponses<PersonalReservationResponse> getMyReservations(@Auth Authentication authentication) {
        List<PersonalReservationResponse> responses = reservationQueryService.getMyReservations(authentication.getPrincipal());
        return new ApiResponses<>(responses);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> addReservation(@Valid @RequestBody ReservationRequest request,
                                                              @Auth Authentication authentication) {
        ReservationCreateRequest reservationCreateRequest = request.toReservationCreateRequest(authentication.getPrincipal());
        ReservationResponse response = reservationPaymentFacadeService.addReservation(reservationCreateRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservationById(@PathVariable Long id, @RequestParam String reason) {
        reservationPaymentFacadeService.cancelReservation(new ReservationCancelRequest(id, reason));
    }
}
