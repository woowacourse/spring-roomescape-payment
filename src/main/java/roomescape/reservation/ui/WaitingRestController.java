package roomescape.reservation.ui;

import static roomescape.auth.domain.AuthRole.ADMIN;
import static roomescape.auth.domain.AuthRole.MEMBER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.domain.MemberAuthInfo;
import roomescape.auth.domain.RequiresRole;
import roomescape.reservation.application.WaitingService;
import roomescape.reservation.ui.dto.request.CreateWaitingRequest;
import roomescape.reservation.ui.dto.response.WaitingResponse;
import roomescape.reservation.ui.dto.response.WaitingWithRankResponse;

@Slf4j
@Tag(name = "예약 대기", description = "예약 대기 관련 API")
@RestController
@RequestMapping("/waitings")
@RequiredArgsConstructor
public class WaitingRestController {

    private final WaitingService waitingService;

    @Operation(summary = "예약 대기 생성", description = "예약 대기를 생성합니다.")
    @PostMapping
    @RequiresRole(authRoles = {ADMIN, MEMBER})
    public ResponseEntity<WaitingResponse> createWaiting(
            @RequestBody @Valid final CreateWaitingRequest.ForMember request,
            final MemberAuthInfo memberAuthInfo
    ) {
        log.info("예약 대기 생성 요청 - 사용자 ID: {}", memberAuthInfo.id());

        final WaitingResponse response = waitingService.create(request, memberAuthInfo.id());

        log.info("예약 대기 생성 완료 - 대기 ID: {}, 사용자 ID: {}", response.id(), memberAuthInfo.id());

        return ResponseEntity.created(URI.create("/waitings/" + response.id()))
                .body(response);
    }

    @Operation(summary = "예약 대기 삭제", description = "예약 대기를 삭제합니다.")
    @DeleteMapping("/{id}")
    @RequiresRole(authRoles = {ADMIN, MEMBER})
    public ResponseEntity<Void> deleteWaiting(
            @PathVariable final Long id,
            final MemberAuthInfo memberAuthInfo
    ) {
        log.info("예약 대기 삭제 요청 - 대기 ID: {}, 사용자 ID: {}", id, memberAuthInfo.id());

        waitingService.deleteIfOwner(id, memberAuthInfo.id());

        log.info("예약 대기 삭제 완료 - 대기 ID: {}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "자신의 예약 대기 조회", description = "자신의 예약 대기 목록을 조회합니다.")
    @GetMapping("/mine")
    @RequiresRole(authRoles = {ADMIN, MEMBER})
    public ResponseEntity<List<WaitingWithRankResponse.ForMember>> findAllWaitingWithRankByMemberId(
            final MemberAuthInfo memberAuthInfo
    ) {
        log.info("예약 대기 목록 조회 요청 - 사용자 ID: {}", memberAuthInfo.id());

        final List<WaitingWithRankResponse.ForMember> waitings =
                waitingService.findAllWaitingWithRankByMemberId(memberAuthInfo.id());

        log.info("예약 대기 목록 조회 완료 - 사용자 ID: {}, 개수: {}", memberAuthInfo.id(), waitings.size());

        return ResponseEntity.ok(waitings);
    }
}
