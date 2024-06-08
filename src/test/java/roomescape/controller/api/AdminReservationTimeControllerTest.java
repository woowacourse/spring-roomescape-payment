package roomescape.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import roomescape.controller.dto.CreateTimeRequest;
import roomescape.controller.dto.CreateTimeResponse;
import roomescape.controller.dto.FindTimeResponse;
import roomescape.global.argumentresolver.AuthenticationPrincipalArgumentResolver;
import roomescape.global.auth.CheckRoleInterceptor;
import roomescape.global.auth.CheckUserInterceptor;
import roomescape.service.ReservationTimeService;

@AutoConfigureRestDocs
@WebMvcTest(AdminReservationTimeController.class)
class AdminReservationTimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationTimeService reservationTimeService;

    @MockBean
    private AuthenticationPrincipalArgumentResolver argumentResolver;

    @MockBean
    private CheckRoleInterceptor checkRoleInterceptor;

    @MockBean
    private CheckUserInterceptor checkUserInterceptor;

    @BeforeEach
    void setUp() {
        given(checkRoleInterceptor.preHandle(any(), any(), any()))
            .willReturn(true);
    }

    @DisplayName("어드민 시간 저장")
    @Test
    void save() throws Exception {
        given(reservationTimeService.save(any()))
            .willReturn(new CreateTimeResponse(1L, LocalTime.parse("10:00")));

        String request = objectMapper.writeValueAsString(new CreateTimeRequest("10:00"));

        mockMvc.perform(post("/admin/times")
                .content(request)
                .contentType(APPLICATION_JSON))
            .andDo(print())
            .andDo(document("admin/times/save",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())))
            .andExpect(status().isCreated());
    }

    @DisplayName("어드민 시간 삭제")
    @Test
    void delete() throws Exception {
        doNothing()
            .when(reservationTimeService)
            .delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/times/1"))
            .andDo(print())
            .andDo(document("admin/times/delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())))
            .andExpect(status().isNoContent());
    }

    @DisplayName("어드민 전체 시간 조회")
    @Test
    void findAll() throws Exception {
        given(reservationTimeService.findAll())
            .willReturn(List.of(
                new FindTimeResponse(1L, LocalTime.parse("10:00")),
                new FindTimeResponse(2L, LocalTime.parse("11:00"))
            ));

        mockMvc.perform(get("/admin/times"))
            .andDo(print())
            .andDo(document("admin/times/findAll",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())))
            .andExpect(status().isOk());
    }
}
