package roomescape.view.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.auth.annotation.Auth;
import roomescape.member.domain.MemberRole;

@Tag(name = "어드민 뷰 컨트롤러", description = "어드민만 접근 가능한 뷰 제공")
@Controller
@Auth(roles = MemberRole.ADMIN)
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping
    public String mainPage() {
        return "admin/index";
    }

    @GetMapping("/reservation")
    public String reservationPage() {
        return "admin/reservation-new";
    }

    @GetMapping({"/time"})
    public String reservationTimePage() {
        return "admin/time";
    }

    @GetMapping("/theme")
    public String themePage() {
        return "admin/theme";
    }

    @GetMapping("/waiting")
    public String waitingPage() {
        return "admin/waiting";
    }
}
