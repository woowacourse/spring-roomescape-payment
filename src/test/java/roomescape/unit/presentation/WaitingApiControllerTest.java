package roomescape.unit.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.auth.AuthToken;
import roomescape.auth.LoginInfo;
import roomescape.auth.jwt.JJWTJwtUtil;
import roomescape.business.model.entity.Member;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.WaitingService;
import roomescape.presentation.api.WaitingApiController;
import roomescape.presentation.dto.request.WaitingRequest;
import roomescape.presentation.dto.response.MemberResponse;
import roomescape.presentation.dto.response.ThemeResponse;
import roomescape.presentation.dto.response.TimeSlotResponse;
import roomescape.presentation.dto.response.WaitingResponse;
import roomescape.presentation.dto.response.WaitingWithRankResponse;

@WebMvcTest(value = {WaitingApiController.class, JJWTJwtUtil.class})
@ExtendWith(RestDocumentationExtension.class)
class WaitingApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JJWTJwtUtil jwtUtil;

    @MockitoBean
    private WaitingService waitingService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
    }

    @Test
    void 대기_생성에_성공한다() throws Exception {
        // given
        WaitingRequest request = new WaitingRequest(
                LocalDate.now().plusDays(1),
                "timeId1",
                "themeId1");
        WaitingResponse response = new WaitingResponse(
                "waitingId1",
                new MemberResponse("memberId1", "name", "email1@domain.com"),
                LocalDate.of(2025, 1, 1),
                new TimeSlotResponse("timeSlotId1", LocalTime.of(9, 0)),
                new ThemeResponse("themeId1", "theme1", "description1", "thumbnail1")
        );
        AuthToken token = jwtUtil.createToken(
                Member.create("name", "email1@domain.com", "password1"));
        given(waitingService.createWaiting(any(LoginInfo.class), any(WaitingRequest.class))).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(post("/waitings")
                .cookie(new Cookie("authToken", token.value()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/waitings/waitingId1"))
                .andExpectAll(
                        jsonPath("$.id").value("waitingId1"),
                        jsonPath("$.date").value("2025-01-01"))
                .andDo(document("create-waiting",
                        requestFields(
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("timeId").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("themeId").type(JsonFieldType.STRING).description("테마 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.STRING).description("예약 대기 ID"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("theme").type(JsonFieldType.OBJECT).description("테마 정보"),
                                fieldWithPath("theme.id").type(JsonFieldType.STRING).description("테마 ID"),
                                fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 URL"),
                                fieldWithPath("user").type(JsonFieldType.OBJECT).description("대기자 정보"),
                                fieldWithPath("user.id").type(JsonFieldType.STRING).description("대기자 ID"),
                                fieldWithPath("user.name").type(JsonFieldType.STRING).description("대기자 이름"),
                                fieldWithPath("user.email").type(JsonFieldType.STRING).description("대기자 이메일"),
                                fieldWithPath("time").type(JsonFieldType.OBJECT).description("예약 시간 정보"),
                                fieldWithPath("time.id").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("time.startAt").type(JsonFieldType.STRING).description("예약 시간")
                        )
                ));
    }

    @Test
    void 예약_대기_전체_조회에_성공한다() throws Exception {
        // given
        WaitingResponse waiting1 = new WaitingResponse(
                "waitingId1",
                new MemberResponse("memberId1", "name", "email1@domain.com"),
                LocalDate.of(2025, 1, 1),
                new TimeSlotResponse("timeSlotId1", LocalTime.of(9, 0)),
                new ThemeResponse("themeId1", "theme1", "description1", "thumbnail1")
        );
        List<WaitingResponse> response = List.of(waiting1);
        AuthToken token = jwtUtil.createToken(
                Member.restore("name", UserRole.ADMIN.name(), "admin", "email1@domain.com", "password1"));
        given(waitingService.findAllWaitings()).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(get("/admin/waitings")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$[0].id").value("waitingId1"),
                        jsonPath("$[0].date").value("2025-01-01"))
                .andDo(document("get-all-waitings",
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 대기 리스트"),
                                fieldWithPath("[].id").type(JsonFieldType.STRING).description("예약 대기 ID"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("[].theme").type(JsonFieldType.OBJECT).description("테마 정보"),
                                fieldWithPath("[].theme.id").type(JsonFieldType.STRING).description("테마 ID"),
                                fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING)
                                        .description("테마 썸네일 URL"),
                                fieldWithPath("[].user").type(JsonFieldType.OBJECT).description("대기자 정보"),
                                fieldWithPath("[].user.id").type(JsonFieldType.STRING).description("대기자 ID"),
                                fieldWithPath("[].user.name").type(JsonFieldType.STRING).description("대기자 이름"),
                                fieldWithPath("[].user.email").type(JsonFieldType.STRING).description("대기자 이메일"),
                                fieldWithPath("[].time").type(JsonFieldType.OBJECT).description("예약 시간 정보"),
                                fieldWithPath("[].time.id").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("[].time.startAt").type(JsonFieldType.STRING).description("예약 시간")
                        )
                ));
    }

    @Test
    void 대기_삭제에_성공한다() throws Exception {
        // given
        AuthToken token = jwtUtil.createToken(
                Member.create("name", "email1@domain.com", "password1"));
        // when
        ResultActions result = mockMvc.perform(delete("/waitings/1")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isNoContent())
                .andDo(document("delete-waiting"));
    }

    @Test
    void 내_대기_조회에_성공한다() throws Exception {
        // given
        WaitingWithRankResponse waiting1 = new WaitingWithRankResponse(
                "waitingId1",
                new MemberResponse("memberId1", "name", "email1@domain.com"),
                LocalDate.of(2025, 1, 1),
                new TimeSlotResponse("timeSlotId1", LocalTime.of(9, 0)),
                new ThemeResponse("themeId1", "theme1", "description1", "thumbnail1"),
                1L
        );
        List<WaitingWithRankResponse> response = List.of(waiting1);
        AuthToken token = jwtUtil.createToken(
                Member.create("name", "email1@domain.com", "password1"));
        given(waitingService.getMyWaitings(anyString())).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(get("/waitings/me")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$[0].id").value("waitingId1"),
                        jsonPath("$[0].date").value("2025-01-01"),
                        jsonPath("$[0].aheadCount").value(1))
                .andDo(document("get-my-waitings",
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("대기 리스트"),
                                fieldWithPath("[].id").type(JsonFieldType.STRING).description("예약 대기 ID"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("[].theme").type(JsonFieldType.OBJECT).description("테마 정보"),
                                fieldWithPath("[].theme.id").type(JsonFieldType.STRING).description("테마 ID"),
                                fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING)
                                        .description("테마 썸네일 URL"),
                                fieldWithPath("[].member").type(JsonFieldType.OBJECT).description("대기자 정보"),
                                fieldWithPath("[].member.id").type(JsonFieldType.STRING).description("대기자 ID"),
                                fieldWithPath("[].member.name").type(JsonFieldType.STRING).description("대기자 이름"),
                                fieldWithPath("[].member.email").type(JsonFieldType.STRING).description("대기자 이메일"),
                                fieldWithPath("[].time").type(JsonFieldType.OBJECT).description("예약 시간 정보"),
                                fieldWithPath("[].time.id").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("[].time.startAt").type(JsonFieldType.STRING).description("예약 시간"),
                                fieldWithPath("[].aheadCount").type(JsonFieldType.NUMBER).description("대기 순위")
                        )
                ));
    }
}