package roomescape.documentaion;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public class ReservationResponseSnippets {

    public static ResponseFieldsSnippet RESERVATION_RESPONSE_ARRAY_SNIPPETS() {
        return responseFields(
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                fieldWithPath("[].memberName").type(JsonFieldType.STRING).description("예약자 이름"),
                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜"),
                fieldWithPath("[].time.id").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                fieldWithPath("[].time.startAt").type(JsonFieldType.STRING)
                        .description("예약 시간(10분 단위) ex) 13:00"),
                fieldWithPath("[].theme.id").type(JsonFieldType.NUMBER).description("테마 식별자"),
                fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름")
        );
    }

    public static ResponseFieldsSnippet RESERVATION_RESPONSE_SINGLE_SNIPPETS() {
        return responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                fieldWithPath("memberName").type(JsonFieldType.STRING).description("예약자 이름"),
                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                fieldWithPath("time.startAt").type(JsonFieldType.STRING)
                        .description("예약 시간(10분 단위) ex) 13:00"),
                fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 식별자"),
                fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름")
        );
    }
}
