package roomescape.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.response.ThemeResponse;
import roomescape.service.RankService;

@Tag(name = "Rank", description = "Operations related to rank theme by popularity")
@RestController
@RequestMapping("/ranks")
public class RankingController {

    private final RankService rankService;

    public RankingController(RankService rankService) {
        this.rankService = rankService;
    }

    @Operation(
            summary = "Find popular themes",
            description = "Retrieve a list of ten popular themes for last two weeks",
            tags = {"Rank API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ThemeResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findTenPopularThemes() {
        return ResponseEntity.ok(rankService.findPopularThemes());
    }
}
