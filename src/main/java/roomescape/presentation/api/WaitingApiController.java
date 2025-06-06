package roomescape.presentation.api;

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
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AuthRequired;
import roomescape.auth.LoginInfo;
import roomescape.auth.Role;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.WaitingService;
import roomescape.presentation.dto.request.WaitingRequest;
import roomescape.presentation.dto.response.WaitingResponse;
import roomescape.presentation.dto.response.WaitingWithRankResponse;

@RestController
@RequiredArgsConstructor
public class WaitingApiController {

    private final WaitingService waitingService;

    @PostMapping("/waitings")
    @AuthRequired
    public ResponseEntity<WaitingResponse> createReservation(@RequestBody @Valid WaitingRequest request,
                                                             LoginInfo loginInfo) {
        WaitingResponse response = waitingService.createWaiting(loginInfo, request);
        return ResponseEntity.created(URI.create("/waitings/" + response.id())).body(response);
    }

    @GetMapping("/admin/waitings")
    @AuthRequired
    @Role(UserRole.ADMIN)
    public List<WaitingResponse> getAllWaitings() {
        return waitingService.findAllWaitings();
    }

    @DeleteMapping("/waitings/{id}")
    @AuthRequired
    @Role(UserRole.ADMIN)
    public ResponseEntity<Void> deleteWaiting(@PathVariable String id) {
        waitingService.deleteWaitingById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/waitings/me")
    @AuthRequired
    public List<WaitingWithRankResponse> getMyReservations(LoginInfo loginInfo) {
        return waitingService.getMyWaitings(loginInfo.id());
    }
}
