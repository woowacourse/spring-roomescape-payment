package roomescape.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoggedInMember;
import roomescape.waiting.dto.WaitingCreateRequest;
import roomescape.waiting.dto.WaitingResponse;
import roomescape.waiting.service.WaitingService;

@Tag(name = "대기 API", description = "예약 대기 API 입니다.")
@RestController
@RequestMapping("/waitings")
public class WaitingController {
    private final WaitingService service;

    public WaitingController(WaitingService service) {
        this.service = service;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WaitingResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = IllegalArgumentException.class)))})
    @Operation(summary = "대기 탐색", description = "대기를 탐색합니다.")
    @GetMapping
    public List<WaitingResponse> findWaitings() {
        return service.findWaitings();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WaitingResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = IllegalArgumentException.class)))})
    @Operation(summary = "대기 생성", description = "대기를 생성합니다.")
    @PostMapping
    public ResponseEntity<WaitingResponse> createWaiting(
            @RequestBody WaitingCreateRequest request,
            LoggedInMember member) {
        WaitingResponse response = service.createWaiting(request, member.id());

        URI location = URI.create("/waitings/" + response.id());
        return ResponseEntity.created(location)
                .body(response);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500", description = "해당 대기는 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = IllegalArgumentException.class)))})
    @Operation(summary = "대기 삭제", description = "단일 대기를 삭제 합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWaiting(@PathVariable Long id) {
        service.deleteWait(id);
    }
}
