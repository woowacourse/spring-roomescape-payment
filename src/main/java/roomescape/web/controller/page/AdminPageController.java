package roomescape.web.controller.page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "관리자 페이지")
@Controller
@RequestMapping("/admin")
class AdminPageController {

    @Operation(summary = "기본 페이지")
    @GetMapping
    public String home() {
        return "admin/index";
    }

    @Operation(summary = "방탈출 예약 페이지", description = "확정된 예약 조회 및 관리할 수 있는 페이지를 반환한다.")
    @GetMapping("/reservation")
    public String reservation() {
        return "admin/reservation-new";
    }

    @Operation(summary = "예약 대기 관리 페이지", description = "대기중인 예약을 관리하는 페이지를 반환한다.")
    @GetMapping("/waiting")
    public String waiting() {
        return "admin/waiting";
    }

    @Operation(summary = "시간 관리 페이지", description = "예약 시간을 관리하는 페이지를 반환한다.")
    @GetMapping("/time")
    public String time() {
        return "admin/time";
    }

    @Operation(summary = "테마 관리 페이지", description = "방탈출 테마를 관리하는 페이지를 반환한다.")
    @GetMapping("/theme")
    public String theme() {
        return "admin/theme";
    }
}
