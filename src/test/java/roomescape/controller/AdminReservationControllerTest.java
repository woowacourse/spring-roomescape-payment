package roomescape.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import roomescape.auth.AuthenticationExtractor;
import roomescape.controller.admin.AdminReservationController;
import roomescape.domain.member.Role;
import roomescape.service.auth.AuthService;
import roomescape.service.dto.request.LoginMember;
import roomescape.service.dto.request.ReservationSaveRequest;
import roomescape.service.dto.response.MemberResponse;
import roomescape.service.dto.response.ReservationResponse;
import roomescape.service.dto.response.ReservationTimeResponse;
import roomescape.service.dto.response.ThemeResponse;
import roomescape.service.reservation.ReservationService;

import javax.management.openmbean.SimpleType;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.IntegrationTestSupport.ADMIN_TOKEN;

@Import({AuthService.class, AuthenticationExtractor.class})
@AutoConfigureRestDocs
@WebMvcTest(controllers = AdminReservationController.class)
@AutoConfigureMockMvc
@ExtendWith({MockitoExtension.class})
public class AdminReservationControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private ReservationService reservationService;


    @Test
    @DisplayName("어드민의 예약 생성")
    public void saveReservation_201() throws Exception {
        //given
        ReservationSaveRequest request = new ReservationSaveRequest(
                1L,
                LocalDate.now().plusDays(1),
                1L,
                1L
        );
        LoginMember loginMember = new LoginMember(1L, "admin", Role.ADMIN);

        ReservationResponse response = new ReservationResponse(
                1L,
                new MemberResponse(1L, "user", "USER"),
                LocalDate.now().plusDays(1),
                new ReservationTimeResponse(1L, LocalTime.of(9, 0, 0)),
                new ThemeResponse(1L, "테마1", "테마 설명", "섬네일 링크")
        );

        Mockito.when(authService.getTokenName()).thenReturn("token");
        Mockito.when(authService.findMemberByToken(any())).thenReturn(loginMember);
        Mockito.when(reservationService.saveReservation(any()))
                .thenReturn(response);

        mockMvc.perform(post("/admin/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", loginMember)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.member.id").value(1L))
                .andExpect(jsonPath("$.time.id").value(1L))
                .andExpect(jsonPath("$.theme.id").value(1L))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(
                                modifyHeaders()
                                        .remove("Content-Length")
                                        .remove("Host"),
                                prettyPrint()),
                        preprocessResponse(
                                modifyHeaders()
                                        .remove("Content-Length")
                                        .remove("X-Content-Type-Options")
                                        .remove("X-XSS-Protection")
                                        .remove("Cache-Control")
                                        .remove("Pragma")
                                        .remove("Expires")
                                        .remove("X-Frame-Options"),
                                prettyPrint()),
                        requestFields(
                                fieldWithPath("memberId")
                                        .type(SimpleType.LONG)
                                        .description("예약 회원 아이디")
                                        .attributes(new Attributes.Attribute("constraints", "1이상의 양수입니다.")),
                                fieldWithPath("date")
                                        .type(SimpleType.DATE)
                                        .description("예약 날짜")
                                        .attributes(new Attributes.Attribute("constraints", "오늘 이후의 날짜만 가능합니다.")),
                                fieldWithPath("timeId")
                                        .type(SimpleType.LONG)
                                        .description("예약 시간 아이디")
                                        .attributes(new Attributes.Attribute("constraints", "예약날짜가 오늘이라면 현재 이후의 시간 아이디만 가능합니다.")),
                                fieldWithPath("themeId")
                                        .type(SimpleType.LONG)
                                        .description("테마 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(SimpleType.STRING)
                                        .description("예약 아이디")
                                        .attributes(new Attributes.Attribute("constraints", "양수의 예약 아이디입니다")),
                                fieldWithPath("member")
                                        .type(MemberResponse.class)
                                        .description("예약한 회원 정보"),
                                fieldWithPath("member.id")
                                        .type(SimpleType.LONG)
                                        .description("회원 id"),
                                fieldWithPath("member.name")
                                        .type(SimpleType.STRING)
                                        .description("회원 이름"),
                                fieldWithPath("member.role")
                                        .type(SimpleType.LONG)
                                        .description("회원 권한")
                                        .attributes(new Attributes.Attribute("constraint", "ADMIN: 어드민, USER : 사용자")),
                                fieldWithPath("date")
                                        .type(SimpleType.DATE)
                                        .description("예약 날짜")
                                        .attributes(new Attributes.Attribute("constraints", "예약된 날짜(오늘 이후만 가능)")),
                                fieldWithPath("time")
                                        .type(ReservationTimeResponse.class)
                                        .description("예약된 시간 정보"),
                                fieldWithPath("time.id")
                                        .type(SimpleType.LONG)
                                        .description("예약 시간 id"),
                                fieldWithPath("time.startAt")
                                        .type(LocalTime.class)
                                        .description("예약 시간"),
                                fieldWithPath("theme")
                                        .type(SimpleType.STRING)
                                        .description("예약된 테마 정보"),
                                fieldWithPath("theme.id")
                                        .type(SimpleType.LONG)
                                        .description("테마 id"),
                                fieldWithPath("theme.name")
                                        .type(SimpleType.STRING)
                                        .description("테마 이름"),
                                fieldWithPath("theme.description")
                                        .type(SimpleType.STRING)
                                        .description("테마 설명"),
                                fieldWithPath("theme.thumbnail")
                                        .type(SimpleType.STRING)
                                        .description("테마 썸네일 링크")
                        )
                ));
    }


}

