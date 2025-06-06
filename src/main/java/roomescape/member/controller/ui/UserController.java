package roomescape.member.controller.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import roomescape.member.auth.PermitAll;
import roomescape.member.auth.RoleRequired;
import roomescape.member.domain.Role;

@RequiredArgsConstructor
@Controller
public class UserController {

    @PermitAll
    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @PermitAll
    @GetMapping("/signup")
    public String getSignupPage() {
        return "signup";
    }

    @RoleRequired({Role.ADMIN, Role.MEMBER})
    @GetMapping("/reservation")
    public String getReservationPage() {
        return "reservation";
    }

    @RoleRequired({Role.ADMIN, Role.MEMBER})
    @GetMapping("/reservation-mine")
    public String getMyReservationPage() {
        return "reservation-mine";
    }
}
