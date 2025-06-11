package roomescape.integration.helper;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;

public class RestDocsFieldSnippets {

    public static class Auth {

        public static final List<FieldDescriptor> MEMBER_LOGIN_REQUEST_FIELDS = List.of(
                fieldWithPath("email").description("멤버 이메일"),
                fieldWithPath("password").description("멤버 비밀번호")
        );

        public static final List<FieldDescriptor> CHECK_MEMBER_LOGIN_RESPONSE_FIELDS = List.of(
                fieldWithPath("name").description("멤버 이름")
        );

        public static final List<FieldDescriptor> ADMIN_LOGIN_REQUEST_FIELDS = List.of(
                fieldWithPath("email").description("어드민 이메일"),
                fieldWithPath("password").description("어드민 비밀번호")
        );

        public static final List<FieldDescriptor> CHECK_ADMIN_LOGIN_RESPONSE_FIELDS = List.of(
                fieldWithPath("name").description("어드민 이름")
        );
    }

    public static class Reservation {

        public static final List<FieldDescriptor> MEMBER_RESERVATION_REQUEST_FIELDS = List.of(
                fieldWithPath("themeId").description("예약할 테마 ID"),
                fieldWithPath("date").description("예약 날짜 (yyyy-MM-dd)"),
                fieldWithPath("timeId").description("예약 시간 ID"),
                fieldWithPath("paymentKey").description("결제 키"),
                fieldWithPath("orderId").description("주문 ID"),
                fieldWithPath("amount").description("결제 금액")
        );
        
        public static final List<FieldDescriptor> ADMIN_RESERVATION_REQUEST_FIELDS = List.of(
                fieldWithPath("memberId").description("회원 ID"),
                fieldWithPath("themeId").description("테마 ID"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("timeId").description("예약 시간 ID")
        );

        public static final List<FieldDescriptor> RESERVATION_RESPONSE_LIST_FIELDS = List.of(
                fieldWithPath("[].id").description("예약 ID"),
                fieldWithPath("[].date").description("예약 날짜"),
                fieldWithPath("[].time.id").description("시간 ID"),
                fieldWithPath("[].time.startAt").description("시작 시간"),
                fieldWithPath("[].theme.id").description("테마 ID"),
                fieldWithPath("[].theme.name").description("테마 이름"),
                fieldWithPath("[].theme.description").description("테마 설명"),
                fieldWithPath("[].theme.thumbnail").description("테마 썸네일 URL"),
                fieldWithPath("[].member.id").description("회원 ID"),
                fieldWithPath("[].member.name").description("회원 이름")
        );

        public static final List<FieldDescriptor> RESERVATION_RESPONSE_FIELDS = List.of(
                fieldWithPath("id").description("예약 ID"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("time.id").description("시간 ID"),
                fieldWithPath("time.startAt").description("시작 시간"),
                fieldWithPath("theme.id").description("테마 ID"),
                fieldWithPath("theme.name").description("테마 이름"),
                fieldWithPath("theme.description").description("테마 설명"),
                fieldWithPath("theme.thumbnail").description("테마 썸네일 URL"),
                fieldWithPath("member.id").description("회원 ID"),
                fieldWithPath("member.name").description("회원 이름")
        );

        public static final List<FieldDescriptor> RESERVATION_MINE_RESPONSE_LIST_FIELDS = List.of(
                fieldWithPath("[].reservationId").description("예약/대기 ID"),
                fieldWithPath("[].theme").description("테마 이름"),
                fieldWithPath("[].date").description("날짜"),
                fieldWithPath("[].time").description("시간"),
                fieldWithPath("[].status").description("예약 상태"),
                fieldWithPath("[].paymentKey").description("결제 키"),
                fieldWithPath("[].amount").description("결제 금액"),
                fieldWithPath("[].approvedAt").description("결제 승인 일시")
        );

        public static final List<FieldDescriptor> WAITING_REQUEST_FIELDS = List.of(
                fieldWithPath("themeId").description("대기할 테마 ID"),
                fieldWithPath("date").description("대기 날짜 (yyyy-MM-dd)"),
                fieldWithPath("timeId").description("대기 시간 ID")
        );

        public static final List<FieldDescriptor> WAITING_RESPONSE_FIELDS = List.of(
                fieldWithPath("id").description("예약 대기 ID"),
                fieldWithPath("name").description("회원 이름"),
                fieldWithPath("theme").description("테마 이름"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("startAt").description("예약 시작 시간")
        );
    }

    public static class TimeSlot {
        
        public static final List<FieldDescriptor> TIME_SLOT_REQUEST_FIELDS = List.of(
                fieldWithPath("startAt").description("시작 시간 (HH:mm:ss)")
        );

        public static final List<FieldDescriptor> TIME_SLOT_RESPONSE_LIST_FIELDS = List.of(
                fieldWithPath("[].id").description("시간 ID"),
                fieldWithPath("[].startAt").description("시작 시간 (HH:mm:ss)")
        );

        public static final List<FieldDescriptor> TIME_SLOT_RESPONSE_FIELDS = List.of(
                fieldWithPath("id").description("시간 ID"),
                fieldWithPath("startAt").description("시작 시간 (HH:mm:ss)")
        );
    }

    public static class Theme {

        public static final List<FieldDescriptor> THEME_REQUEST_FIELDS = List.of(
                fieldWithPath("name").description("테마 이름"),
                fieldWithPath("description").description("테마 설명"),
                fieldWithPath("thumbnail").description("테마 썸네일 URL")
        );

        public static final List<FieldDescriptor> THEME_RESPONSE_LIST_FIELDS = List.of(
                fieldWithPath("[].id").description("테마 ID"),
                fieldWithPath("[].name").description("테마 이름"),
                fieldWithPath("[].description").description("테마 설명"),
                fieldWithPath("[].thumbnail").description("테마 썸네일 URL")
        );

        public static final List<FieldDescriptor> THEME_RESPONSE_FIELDS = List.of(
                fieldWithPath("id").description("테마 ID"),
                fieldWithPath("name").description("테마 이름"),
                fieldWithPath("description").description("테마 설명"),
                fieldWithPath("thumbnail").description("테마 썸네일 URL")
        );

        public static final List<FieldDescriptor> POPULAR_THEME_RESPONSE_LIST_FIELDS = List.of(
                fieldWithPath("[].name").description("테마 이름"),
                fieldWithPath("[].description").description("테마 설명"),
                fieldWithPath("[].thumbnail").description("테마 썸네일 URL")
        );
    }

    private RestDocsFieldSnippets() {}
}
