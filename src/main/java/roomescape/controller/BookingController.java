package roomescape.controller;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.response.BookResponse;
import roomescape.service.BookService;

@Tag(name = "Booking", description = "Operations related to browse available booking")
@RestController
@RequestMapping("/books")
public class BookingController {

    private final BookService bookService;

    public BookingController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Find available books schedule for a specific date and theme",
            description = "This endpoint finds available books schedule for a specific date and theme for everyone.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{date}/{theme_id}")
    public ResponseEntity<List<BookResponse>> findAvaliableBooks(
            @Parameter(description = "Date in yyyy-MM-dd format", example = "2024-06-10", required = true)
            @PathVariable LocalDate date,
            @Parameter(description = "ID of the theme", example = "1", required = true)
            @PathVariable(value = "theme_id") Long themeId) {
        List<BookResponse> books = bookService.findAvaliableBooks(date, themeId);
        return ResponseEntity.ok(books);
    }
}
