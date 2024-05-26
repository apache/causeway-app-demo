package domainapp.modules.visit.contributions;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.apache.causeway.applib.clock.VirtualClock;
import org.apache.causeway.applib.services.clock.ClockService;

@ExtendWith(MockitoExtension.class)
public class PetOwner_bookVisit_Test {

    @Mock ClockService mockClockService;
    @Mock VirtualClock mockVirtualClock;

    @BeforeEach
    void setup() {
        Mockito.when(mockClockService.getClock()).thenReturn(mockVirtualClock); // <.>
    }

    @Nested
    class default1 {

        @Test
        void defaults_to_9am_tomorrow_morning() {

            // given
            PetOwner_bookVisit mixin = new PetOwner_bookVisit(null);
            mixin.clockService = mockClockService;

            LocalDateTime now = LocalDateTime.of(2024, 5, 26, 16, 37, 45);

            // expecting
            Mockito.when(mockVirtualClock.nowAsLocalDate()).thenReturn(now.toLocalDate());

            // when
            LocalDateTime localDateTime = mixin.default1Act();

            // then
            Assertions.assertThat(localDateTime)
                    .isEqualTo(LocalDateTime.of(2024,5,27,9,0,0));
        }
    }
}
