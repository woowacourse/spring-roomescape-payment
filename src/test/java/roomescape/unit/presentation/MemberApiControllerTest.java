package roomescape.unit.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
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
import roomescape.auth.jwt.JJWTJwtUtil;
import roomescape.auth.jwt.JwtUtil;
import roomescape.business.model.entity.Member;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.MemberService;
import roomescape.presentation.api.MemberApiController;
import roomescape.presentation.dto.request.RegisterRequest;
import roomescape.presentation.dto.response.MemberResponse;

@WebMvcTest(value = {MemberApiController.class, JJWTJwtUtil.class})
@ExtendWith(RestDocumentationExtension.class)
class MemberApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private MemberService memberService;

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
    void 회원_가입에_성공한다() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest("name1", "email1@domain.com", "password1");
        MemberResponse response = new MemberResponse("id1", "name1", "email1@domain.com");
        given(memberService.register(request)).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/members"))
                .andExpect(jsonPath("$.id").value("id1"))
                .andExpect(jsonPath("$.name").value("name1"))
                .andExpect(jsonPath("$.email").value("email1@domain.com"))
                .andDo(document("register",
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.STRING).description("회원 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                        )
                ));
    }

    @Test
    void 회원_전체_조회에_성공한다() throws Exception {
        // given
        MemberResponse member1 = new MemberResponse("id1", "name1", "email1@domain.com");
        List<MemberResponse> response = List.of(member1);
        AuthToken token = jwtUtil.createToken(
                Member.restore("name", UserRole.ADMIN.name(), "admin", "email1@domain.com", "password1"));
        given(memberService.getAll()).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(get("/members")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("id1"))
                .andExpect(jsonPath("$[0].name").value("name1"))
                .andExpect(jsonPath("$[0].email").value("email1@domain.com"))
                .andDo(document("get-all-members",
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.STRING).description("회원 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("[].email").type(JsonFieldType.STRING).description("이메일")
                        )
                ));
    }
}
