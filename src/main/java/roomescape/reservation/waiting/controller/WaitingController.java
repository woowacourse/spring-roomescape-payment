package roomescape.reservation.waiting.controller;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.WaitingCreateRequest;
import roomescape.reservation.dto.response.WaitingResponse;
import roomescape.reservation.waiting.service.WaitingService;

@Slf4j
@RequestMapping("/waitings")
@RestController
public class WaitingController {

    private final WaitingService waitingService;

    public WaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @PostMapping
    public ResponseEntity<WaitingResponse> create(
            @Valid @RequestBody final ReservationRequest request,
            final LoginMember loginMember
    ) {
        log.info("대기 등록 요청: memberId={}, date={}, timeId={}, themeId={}",
                loginMember.id(), request.date(), request.timeId(), request.themeId());
        WaitingCreateRequest createRequest = WaitingCreateRequest.from(request, loginMember);
        WaitingResponse response = waitingService.create(createRequest);
        return ResponseEntity.created(URI.create("/waitings/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{waitingId}")
    public ResponseEntity<Void> delete(@PathVariable("waitingId") final Long waitingId) {
        log.info("대기 삭제 요청: waitingId={}", waitingId);
        waitingService.deleteWaiting(waitingId);
        return ResponseEntity.noContent()
                .build();
    }
}
