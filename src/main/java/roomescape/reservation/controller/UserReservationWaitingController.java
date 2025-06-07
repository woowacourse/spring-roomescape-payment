package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.AuthenticatedMember;
import roomescape.auth.web.resolver.AuthenticationPrincipal;
import roomescape.reservation.application.UserReservationWaitingService;
import roomescape.reservation.controller.dto.request.UserCreateReservationWaitingRequest;

@Tag(name = "waiting", description = "대기 도메인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations/waiting")
public class UserReservationWaitingController {

    private final UserReservationWaitingService userReservationWaitingService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void create(
            @RequestBody @Valid UserCreateReservationWaitingRequest request,
            @AuthenticationPrincipal AuthenticatedMember member
    ) {
        userReservationWaitingService.create(request.toServiceRequest(member.id()));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/cancel")
    public void cancel(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AuthenticatedMember member
    ) {
        userReservationWaitingService.cancel(id, member.id());
    }
}
