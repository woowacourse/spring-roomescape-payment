package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.AuthenticationPrincipal;
import roomescape.global.auth.annotation.RoleRequired;
import roomescape.global.auth.dto.LoginMember;
import roomescape.member.entity.RoleType;
import roomescape.reservation.dto.request.WaitingCreateRequest;
import roomescape.reservation.dto.response.WaitingResponse;
import roomescape.reservation.service.WaitingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/waitings")
@Tag(name = "대기", description = "대기 관련 API")
public class WaitingController {

    private final WaitingService waitingService;

    @Operation(summary = "대기 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @PostMapping
    @RoleRequired(roleType = {RoleType.USER, RoleType.ADMIN})
    public ResponseEntity<WaitingResponse> createWaiting(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginMember loginMember,
            @RequestBody @Valid WaitingCreateRequest request
    ) {
        WaitingResponse response = waitingService.createWaiting(loginMember.id(), request);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "대기 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    @DeleteMapping("/{id}")
    @RoleRequired(roleType = {RoleType.USER, RoleType.ADMIN})
    public ResponseEntity<Void> deleteWaiting(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginMember loginMember,
            @PathVariable("id") long id
    ) {
        waitingService.deleteWaiting(loginMember.id(), id);
        return ResponseEntity.noContent().build();
    }
}
