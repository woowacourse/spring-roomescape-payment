package roomescape.web.controller.page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "사용자 페이지")
@Controller
class FrontPageController {

    @Operation(summary = "예약 페이지", description = "방탈출 예약 및 결제 페이지를 반환한다.")
    @GetMapping("/reservation")
    public String reservation(){
        return "reservation";
    }

    @Operation(summary = "내 예약 페이지", description = "로그인 사용자의 예약 관리 페이지를 반환한다.")
    @GetMapping("/reservation-mine")
    public String reservationMine() {
        return "reservation-mine";
    }

    @Operation(summary = "로그인 페이지", description = "로그인 페이지를 반환한다.")
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
