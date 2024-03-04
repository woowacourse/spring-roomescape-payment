package roomescape.theme;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class ThemeController {
    private ThemeRepository themeRepository;

    public ThemeController(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    @PostMapping("/themes")
    public ResponseEntity<Theme> createTheme(@RequestBody Theme theme) {
        Theme newTheme = themeRepository.save(theme);
        return ResponseEntity.created(URI.create("/themes/" + newTheme.getId())).body(newTheme);
    }

    @GetMapping("/themes")
    public ResponseEntity<List<Theme>> list() {
        return ResponseEntity.ok(themeRepository.findAll());
    }

    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable Long id) {
        themeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
