package com.mogaco.project.meet.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MeetTest {
    private static final String TIME = "14:00 ~ 16:00";
    private static final String TITLE = "let's study";
    private static final String MESSAGE = "share with me";
    private static final LocalDate STARTED_AT = LocalDate.of(2021, 4, 10);
    private static final int COUNT = 3;

    @Test
    void create() {
        final Location givenLocation = new Location("seoul", "hongdae");
        final Message givenMessage = new Message(TITLE, MESSAGE);
        final MeetTime givenMeetTime = new MeetTime(STARTED_AT, TIME);

        Meet meet = Meet.builder()
                .location(givenLocation)
                .message(givenMessage)
                .count(COUNT)
                .meetTime(givenMeetTime)
                .build();

        assertThat(meet.getCount()).isEqualTo(COUNT);
        assertThat(meet.getLocation()).isEqualTo(givenLocation);
        assertThat(meet.getMessage()).isEqualTo(givenMessage);
        assertThat(meet.getMeetTime()).isEqualTo(givenMeetTime);
    }
}
