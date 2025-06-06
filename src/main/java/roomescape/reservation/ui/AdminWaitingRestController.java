package roomescape.reservation.ui;


import static roomescape.auth.domain.AuthRole.ADMIN;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.domain.RequiresRole;
import roomescape.reservation.application.AdminWaitingService;
import roomescape.reservation.ui.dto.request.CreateWaitingRequest;
import roomescape.reservation.ui.dto.response.WaitingResponse;
import roomescape.reservation.ui.dto.response.WaitingWithRankResponse;

@Tag(name = "관리자 예약 대기", description = "관리자의 예약 대기 api")
@RestController
@RequestMapping("/admin/waitings")
@RequiresRole(authRoles = {ADMIN})
@RequiredArgsConstructor
public class AdminWaitingRestController {

    private final AdminWaitingService adminWaitingService;

    @Operation(summary = "관리자 예약 대기 생성", description = "관리자 예약 대기를 생성합니다.")
    @PostMapping
    public ResponseEntity<WaitingResponse> create(
            @RequestBody @Valid final CreateWaitingRequest request
    ) {
        final WaitingResponse response = adminWaitingService.create(request);

        return ResponseEntity.created(URI.create("/admin/waitings/" + response.id()))
                .body(response);
    }

    @Operation(summary = "관리자 예약 대기 삭제", description = "관리자 예약 대기를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deny(
            @PathVariable final Long id
    ) {
        adminWaitingService.deleteAsAdmin(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "관리자 모든 예약 대기 조회", description = "관리자 모든 예약 대기를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<WaitingWithRankResponse>> findAllWaitingWithRank() {
        final List<WaitingWithRankResponse> responses = adminWaitingService.findAllWaitingWithRank();

        return ResponseEntity.ok(responses);
    }
}
