package roomescape.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientPageController {
    @GetMapping("/")
    public String showPopularThemePage() {
        return "index";
    }

    @GetMapping("/reservation")
    public String showReservationPage() {
        return "reservation";
    }

    @GetMapping("/reservation-mine")
    public String showReservationMinePage() {
        return "reservation-mine";
    }
}
