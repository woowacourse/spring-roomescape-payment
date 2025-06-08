package roomescape.reservation.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.RequireRole;
import roomescape.global.auth.dto.UserInfo;
import roomescape.member.domain.MemberRole;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.WaitingFacadeService;

@Slf4j
@RestController
public class WaitingController {

    private final WaitingFacadeService waitingFacadeService;

    public WaitingController(WaitingFacadeService waitingFacadeService) {
        this.waitingFacadeService = waitingFacadeService;
    }


    @GetMapping("/waiting")
    public ResponseEntity<List<ReservationResponse>> findWaitings(
    ) {
        return ResponseEntity.ok(waitingFacadeService.findWaitings());
    }

    @RequireRole(MemberRole.USER)
    @DeleteMapping("/waiting/{id}")
    public ResponseEntity<Void> deleteWaiting(
            @PathVariable("id") Long id
    ) {
        log.info("대기 삭제 시도 waitingId = {}", id);
        waitingFacadeService.deleteWaiting(id);
        log.info("대기 삭제 성공 waitingId = {}", id);
        return ResponseEntity.noContent().build();
    }

    @RequireRole(MemberRole.USER)
    @PostMapping("/waiting")
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody ReservationRequest request,
            UserInfo userInfo
    ) {
        log.info("대기 생성 시도 memberId = {}, date = {}, timeId = {}, themeId = {}", userInfo.id(), request.date(),
                request.timeId(), request.themeId());
        ReservationResponse response = waitingFacadeService.createWaiting(request, userInfo.id());
        log.info("대기 생성 성공 memberId = {}, waitingId = {}", userInfo.id(), response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
