package roomescape.controller;

import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.WaitingResponse;
import roomescape.service.ReservationService;
import roomescape.service.WaitingService;

@Tag(name = "Admin", description = "Operations related to admins")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ReservationService reservationService;
    private final WaitingService waitingService;

    public AdminController(ReservationService reservationService, WaitingService waitingService) {
        this.reservationService = reservationService;
        this.waitingService = waitingService;
    }

    @Operation(
            summary = "Create a new reservation by admin",
            description = "This endpoint allows an admin to create a new reservation.",
            tags = {"Reservation API"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createAdminReservation(
            @Parameter(description = "Reservation request payload", required = true)
            @RequestBody ReservationRequest request) {
        ReservationResponse reservationResponse = reservationService.createByAdmin(request);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(
            summary = "Find all waiting reservations",
            description = "This endpoint retrieves all waiting reservations.",
            tags = {"Waiting API"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved waiting reservations",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WaitingResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/waitings")
    public ResponseEntity<List<WaitingResponse>> findAllWaitings() {
        List<WaitingResponse> waitings = waitingService.findEntireReservations();
        return ResponseEntity.ok(waitings);
    }

    @Operation(
            summary = "Cancel a waiting reservation",
            description = "This endpoint cancels a waiting reservation by its ID.",
            tags = {"Waiting API"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Waiting reservation canceled",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Waiting reservation not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/waitings/{id}")
    public ResponseEntity<Void> cancelWaiting(
            @Parameter(description = "ID of the waiting to be retrieved", example = "1", required = true)
            @PathVariable Long id) {
        waitingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
