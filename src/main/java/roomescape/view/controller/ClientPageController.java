package roomescape.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import roomescape.system.auth.annotation.LoginRequired;

@Controller
public class ClientPageController {

    @GetMapping("/")
    public String showPopularThemePage() {
        return "index";
    }

    @LoginRequired
    @GetMapping("/reservation")
    public String showReservationPage() {
        return "reservation";
    }

    @LoginRequired
    @GetMapping("/reservation-mine")
    public String showReservationMinePage() {
        return "reservation-mine";
    }
}
