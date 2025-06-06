package roomescape.mvc.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import roomescape.mvc.member.domain.Role;
import roomescape.annotation.Authority;

@Controller
public class GeneralViewController {

    @GetMapping
    public String getHomePage() {
        return "index";
    }

    @GetMapping("/reservation")
    public String getReservationPage() {
        return "reservation";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/reservation-mine")
    @Authority(Role.GENERAL)
    public String getReservationMinePage() {
        return "reservation-mine";
    }
}
