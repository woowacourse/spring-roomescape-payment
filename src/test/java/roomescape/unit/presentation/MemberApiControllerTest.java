package roomescape.unit.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import roomescape.auth.AuthToken;
import roomescape.business.model.entity.Member;
import roomescape.business.model.vo.UserRole;
import roomescape.presentation.dto.request.RegisterRequest;
import roomescape.presentation.dto.response.MemberResponse;

class MemberApiControllerTest extends ControllerTest {

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
