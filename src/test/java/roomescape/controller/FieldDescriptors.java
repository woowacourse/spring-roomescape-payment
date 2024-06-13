package roomescape.controller;


import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;

public class FieldDescriptors {

    public static final List<FieldDescriptor> RESERVATION_REQUEST = List.of(
            fieldWithPath("date").type(JsonFieldType.STRING).description("예약할 날짜"),
            fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약할 시간 아이디"),
            fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("예약할 테마 아이디"),
            fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 키의 값"),
            fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문번호"),
            fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제할 금액")
    );

    public static final List<FieldDescriptor> RESERVATION_WAITING_REQUEST = List.of(
            fieldWithPath("date").type(JsonFieldType.STRING).description("예약 대기할 날짜"),
            fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 대기할 시간 아이디"),
            fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("예약 대기할 테마 아이디")
    );

    public static final List<FieldDescriptor> RESERVATION_RESPONSE = List.of(
            fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 아이디"),
            fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
            fieldWithPath("name").type(JsonFieldType.STRING).description("예약자 이름"),
            fieldWithPath("startAt").type(JsonFieldType.STRING).description("예약 시작 시간"),
            fieldWithPath("theme").type(JsonFieldType.STRING).description("예약 테마 이름")
    );

    public static final List<FieldDescriptor> THEME_RESPONSE = List.of(
            fieldWithPath("list[].id").type(JsonFieldType.NUMBER).description("테마 아이디"),
            fieldWithPath("list[].name").type(JsonFieldType.STRING).description("테마 이름"),
            fieldWithPath("list[].description").type(JsonFieldType.STRING).description("테마 설명"),
            fieldWithPath("list[].thumbnail").type(JsonFieldType.STRING).description("테마 썸네일")
    );

    public static final List<FieldDescriptor> AVAILABLE_RESERVATION_TIME_RESPONSE = List.of(
            fieldWithPath("list[].timeId").type(JsonFieldType.NUMBER).description("시간 아이디"),
            fieldWithPath("list[].startAt").type(JsonFieldType.STRING).description("시작 시간"),
            fieldWithPath("list[].alreadyBooked").type(JsonFieldType.BOOLEAN).description("예약 여부")
    );

    public static final List<ParameterDescriptor> AVAILABLE_RESERVATION_TIME_PARAMETER = List.of(
            parameterWithName("date").description("조회할 날짜"),
            parameterWithName("themeId").description("조회할 테마 아이디")
    );
}
