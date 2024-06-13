package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.Reservation;
import roomescape.dto.LoginMember;
import roomescape.dto.request.MemberReservationRequest;
import roomescape.dto.response.ReservationMineResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.service.PaymentService;
import roomescape.service.ReservationService;

@Tag(name = "Reservation", description = "Operations related to manage reservations by user")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(
            summary = "Create reservation by client",
            description = "Create a reservation for a client",
            tags = {"Reservation API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservationByClient(
            @Parameter(description = "The reservation request payload", required = true)
            @Valid @RequestBody MemberReservationRequest memberRequest,
            @Parameter(description = "The logged-in member", required = true) LoginMember member) {
        ReservationResponse reservationResponse = reservationService.createByClient(memberRequest, member);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "Find all reservations",
            description = "Retrieve a list of all reservations",
            tags = {"Reservation API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findReservations() {
        List<ReservationResponse> reservations = reservationService.findAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @Operation(summary = "Find reservations by criteria",
            description = "Retrieve a list of reservations based on provided criteria(member, theme, duration)",
            tags = {"Reservation API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping(params = {"memberId", "themeId", "dateFrom", "dateTo"})
    public ResponseEntity<List<ReservationResponse>> findAllByMemberAndThemeAndPeriod(
            @Parameter(description = "The ID of the member", example = "1")
            @RequestParam(required = false, name = "memberId") Long memberId,
            @Parameter(description = "The ID of the theme", example = "1")
            @RequestParam(required = false, name = "themeId") Long themeId,
            @Parameter(description = "The starting date", example = "2024-01-01")
            @RequestParam(required = false, name = "dateFrom") String dateFrom,
            @Parameter(description = "The ending date", example = "2024-12-31")
            @RequestParam(required = false, name = "dateTo") String dateTo) {
        return ResponseEntity.ok(reservationService.findDistinctReservations(memberId, themeId, dateFrom, dateTo));
    }

    @Operation(
            summary = "Find my reservations",
            description = "Retrieve a list of reservations made by the logged-in member",
            tags = {"Reservation API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationMineResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/mine")
    public ResponseEntity<List<ReservationMineResponse>> findMyReservations(
            @Parameter(description = "The logged-in member", required = true) LoginMember loginMember) {
        List<ReservationMineResponse> myReservationsAndWaitings = reservationService
                .findMyReservationsAndWaitings(loginMember);
        return ResponseEntity.ok(myReservationsAndWaitings);
    }

    @Operation(
            summary = "Delete reservation by ID",
            description = "Delete a reservation by its ID",
            tags = {"Reservation API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @Parameter(description = "The ID of the reservation", example = "1", required = true)
            @PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
