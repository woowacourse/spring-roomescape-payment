package roomescape.fixture;

import java.time.LocalTime;
import org.springframework.test.util.ReflectionTestUtils;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;

public class TimeSlotFixture {

    public static TimeSlot CREATE_TIME_SLOT_1(){
        TimeSlot register = TimeSlot.register(LocalTime.of(10, 0));
        ReflectionTestUtils.setField(register, "id", 1L);
        return register;
    }

    public static TimeSlot CREATE_TIME_SLOT_2(){
        TimeSlot register = TimeSlot.register(LocalTime.of(11, 0));
        ReflectionTestUtils.setField(register, "id", 2L);
        return register;
    }

    public static TimeSlot CREATE_TIME_SLOT_3(){
        TimeSlot register = TimeSlot.register(LocalTime.of(12, 0));
        ReflectionTestUtils.setField(register, "id", 3L);
        return register;
    }

    public static TimeSlot CREATE_TIME_SLOT_4(){
        TimeSlot register = TimeSlot.register(LocalTime.of(13, 0));
        ReflectionTestUtils.setField(register, "id", 4L);
        return register;
    }
}
