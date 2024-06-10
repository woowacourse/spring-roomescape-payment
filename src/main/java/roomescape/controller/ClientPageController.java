package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "User-Page", description = "Loads pages related to users")
@Controller
public class ClientPageController {

    @Operation(
            summary = "Load login page",
            description = "This endpoint loads login page for everyone.",
            tags = {"Page API"})
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @Operation(
            summary = "Load reservation page",
            description = "This endpoint loads reservation page for everyone.",
            tags = {"Page API"})
    @GetMapping("/reservation")
    public String reservationPage() {
        return "reservation";
    }

    @Operation(
            summary = "Load my-reservation page",
            description = "This endpoint loads my-reservation page for everyone.",
            tags = {"Page API"})
    @GetMapping("/reservation-mine")
    public String reservationMinePage() {
        return "reservation-mine";
    }

    @Operation(
            summary = "Load index page",
            description = "This endpoint loads index page for everyone.",
            tags = {"Page API"})
    @GetMapping("/")
    public String indexPage() {
        return "index";
    }
}
