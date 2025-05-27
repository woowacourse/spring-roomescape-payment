package roomescape.order;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.auth.dto.LoginMember;
import roomescape.member.Member;
import roomescape.member.MemberService;
import roomescape.order.dto.OrderCreateRequest;
import roomescape.schedule.Schedule;
import roomescape.schedule.ScheduleService;

@Service
@AllArgsConstructor
public class OrderService {

    private final ScheduleService scheduleService;
    private final MemberService memberService;
    private final OrderRepository orderRepository;

    @Transactional
    public void create(final OrderCreateRequest orderCreateRequest, LoginMember loginMember) {
        final Schedule schedule = scheduleService.getByDateAndTimeIdAndThemeId(
                orderCreateRequest.date(),
                orderCreateRequest.timeId(),
                orderCreateRequest.themeId()
        );
        final Member member = memberService.getByEmail(loginMember.email());
        final Order order = new Order(orderCreateRequest.id(), orderCreateRequest.amount(), PaymentStatus.WAITING, member, schedule);
        orderRepository.save(order);
    }
}
