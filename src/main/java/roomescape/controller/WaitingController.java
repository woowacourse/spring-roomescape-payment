package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.LoginMember;
import roomescape.dto.request.MemberWaitingRequest;
import roomescape.dto.request.WaitingRequest;
import roomescape.dto.response.WaitingResponse;
import roomescape.service.WaitingService;

import java.net.URI;

@Tag(name = "Waiting", description = "Operations related to manage waitings")
@RestController
@RequestMapping("/waitings")
public class WaitingController {

    private final WaitingService waitingService;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(
            summary = "Create waiting by user",
            description = "Create a new waiting request by user",
            tags = {"Waiting API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WaitingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<WaitingResponse> createWaitingByClient(
            @Parameter(description = "The waiting request payload", required = true,
                    schema = @Schema(implementation = MemberWaitingRequest.class))
            @Valid @RequestBody MemberWaitingRequest memberWaitingRequest, LoginMember member) {
        WaitingRequest waitingRequest = WaitingRequest.from(member.id(), memberWaitingRequest);
        WaitingResponse waitingResponse = waitingService.create(waitingRequest);

        return ResponseEntity.created(URI.create("/waitings/" + waitingResponse.id())).body(waitingResponse);
    }

    @Operation(
            summary = "Delete waiting by ID",
            description = "Delete a waiting request by its ID",
            tags = {"Waiting API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaiting(
            @Parameter(description = "The ID of the waiting request", required = true, example = "1")
            @PathVariable Long id) {
        waitingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
