package roomescape.theme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.auth.provider.model.TokenProvider;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.model.ControllerTest;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeRankResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;
import roomescape.vo.Name;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ThemeController.class)
@Import(TokenProvider.class)
public class ThemeControllerTest extends ControllerTest {

    public static final LocalDate TODAY = LocalDate.now();
    private final Theme theme = new Theme(1L, new Name("포레스트"), "공포 테마",
            "https://zerogangnam.com/storage/AVISPw8N2JfMThKvnk3VJzeY9qywIaYd8pTy46Xx.jpg",15000L);
    private final Member admin = new Member(1L, new Name("admin"), "admin1@email.com", "admin1", MemberRole.ADMIN);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ThemeService themeService;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("테마 정보를 정상적으로 저장하는지 확인한다.")
    void saveTheme() throws Exception {
        given(themeService.addTheme(any()))
                .willReturn(ThemeResponse.fromTheme(theme));
        String content = new ObjectMapper()
                .writeValueAsString(new ThemeRequest(theme.getName(), theme.getDescription(), theme.getThumbnail(), theme.getPrice()));

        mockMvc.perform(post("/admin/themes")
                        .content(content)
                        .contentType("application/Json")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(theme.getId()))
                .andExpect(jsonPath("$.name").value(theme.getName()))
                .andExpect(jsonPath("$.thumbnail").value(theme.getThumbnail()))
                .andExpect(jsonPath("$.description").value(theme.getDescription()));
    }

    @Test
    @DisplayName("예약 정보를 정상적으로 불러오는지 롹인한다.")
    void findAllThemes() throws Exception {
        when(themeService.findThemes())
                .thenReturn(List.of(ThemeResponse.fromTheme(theme)));

        mockMvc.perform(get("/themes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(theme.getId()))
                .andExpect(jsonPath("$[0].name").value(theme.getName()))
                .andExpect(jsonPath("$[0].thumbnail").value(theme.getThumbnail()))
                .andExpect(jsonPath("$[0].description").value(theme.getDescription()));
    }

    @Test
    @DisplayName("인기많은 테마 정보를 정상적으로 가져오는지 확인한다.")
    void findTopRankThemes() throws Exception {
        when(themeService.findRankedThemes(TODAY))
                .thenReturn(List.of(ThemeRankResponse.fromTheme(theme)));

        mockMvc.perform(get("/themes/rank?date=" + TODAY))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].thumbnail").value(theme.getThumbnail()))
                .andExpect(jsonPath("$[0].name").value(theme.getName()))
                .andExpect(jsonPath("$[0].description").value(theme.getDescription()));
    }

    @Test
    @DisplayName("예약 정보를 정상적으로 지우는지 확인한다.")
    void deleteTheme() throws Exception {
        mockMvc.perform(delete("/admin/themes/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
