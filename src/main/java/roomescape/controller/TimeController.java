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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.TimeSlotRequest;
import roomescape.dto.response.TimeSlotResponse;
import roomescape.service.TimeService;

@Tag(name = "Time", description = "Operations related to manage times")
@RestController
@RequestMapping("/times")
public class TimeController {

    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @Operation(
            summary = "Find all time slots",
            description = "Retrieve a list of all time slots",
            tags = {"Time API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TimeSlotResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<List<TimeSlotResponse>> findAllTimeSlots() {
        List<TimeSlotResponse> timeSlots = timeService.findAll();
        return ResponseEntity.ok(timeSlots);
    }

    @Operation(
            summary = "Create time slot",
            description = "Create a new time slot",
            tags = {"Time API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TimeSlotResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<TimeSlotResponse> create(
            @Parameter(description = "The time slot request payload", required = true,
                    schema = @Schema(implementation = TimeSlotRequest.class))
            @RequestBody TimeSlotRequest timeSlotRequest) {
        TimeSlotResponse timeSlotResponse = timeService.create(timeSlotRequest);
        return ResponseEntity.created(URI.create("/times/" + timeSlotResponse.id())).body(timeSlotResponse);
    }

    @Operation(
            summary = "Delete time slot by ID",
            description = "Delete a time slot by its ID",
            tags = {"Time API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "The ID of the time slot", required = true, example = "1")
            @PathVariable Long id) {
        timeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
