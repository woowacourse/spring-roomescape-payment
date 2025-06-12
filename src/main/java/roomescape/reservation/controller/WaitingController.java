package roomescape.reservation.controller;

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
import roomescape.common.utils.UriFactory;
import roomescape.member.auth.LoginMember;
import roomescape.member.auth.RoleRequired;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.member.domain.Role;
import roomescape.reservation.controller.dto.CreateWaitingWebRequest;
import roomescape.reservation.controller.dto.ReservationWebResponse;
import roomescape.reservation.controller.dto.ReservationWithStatusResponse;
import roomescape.reservation.service.ReservationService;

@RequiredArgsConstructor
@RestController
public class WaitingController implements SwaggerWaitingController {

    public static final String BASE_PATH = "/waitings";

    private final ReservationService reservationService;

    @PostMapping(BASE_PATH)
    public ResponseEntity<ReservationWithStatusResponse> createWaiting(
            @RequestBody final CreateWaitingWebRequest createWaitingWebRequest,
            @LoginMember MemberInfo memberInfo) {
        final ReservationWithStatusResponse reservationWithStatusResponse = reservationService.createWaiting(
                createWaitingWebRequest,
                memberInfo);
        final URI location = UriFactory.buildPath(BASE_PATH,
                String.valueOf(reservationWithStatusResponse.id()));
        return ResponseEntity.created(location)
                .body(reservationWithStatusResponse);
    }

    @RoleRequired(value = Role.ADMIN)
    @GetMapping(BASE_PATH)
    public List<ReservationWebResponse> getAllWaiting() {
        return reservationService.getAllWaiting();
    }

    @RoleRequired(value = Role.ADMIN)
    @DeleteMapping("/admin" + BASE_PATH + "/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable final Long id) {
        reservationService.deleteWaiting(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(BASE_PATH + "/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable final Long id,
                                              @LoginMember MemberInfo memberInfo) {
        reservationService.deleteWaiting(id, memberInfo);
        return ResponseEntity.noContent().build();
    }
}
