package roomescape.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.controller.config.RestDocsTestSupport;
import roomescape.service.dto.request.MemberJoinRequest;
import roomescape.service.dto.response.MemberResponse;
import roomescape.service.reservation.MemberService;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest extends RestDocsTestSupport {
    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("멤버 가입")
    void save() throws Exception {
        //given
        MemberJoinRequest request = new MemberJoinRequest(
                "email@email.com",
                "password",
                "name"
        );

        MemberResponse response = new MemberResponse(
                1L,
                "userName",
                "USER"
        );
        Mockito.when(memberService.join(any()))
                .thenReturn(response);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.name").value(response.name()))
                .andExpect(jsonPath("$.role").value(response.role()))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("email")
                                        .type(STRING)
                                        .description("회원 이메일")
                                        .attributes(constraints("unique + 이메일 형식")),
                                fieldWithPath("password")
                                        .type(STRING)
                                        .description("패스워드")
                                        .attributes(constraints("unique")),
                                fieldWithPath("name")
                                        .type(STRING)
                                        .description("회원 이름")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(NUMBER)
                                        .description("회원 아이디")
                                        .attributes(constraints("양수의 회원 아이디입니다")),
                                fieldWithPath("name")
                                        .type(STRING)
                                        .description("이름"),
                                fieldWithPath("role")
                                        .type(STRING)
                                        .description("권한")
                                        .attributes(constraints("USER : 일반 사용자%nADMIN : 관리자"))
                        )
                ));
    }
}