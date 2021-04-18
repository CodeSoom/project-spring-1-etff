package com.mogaco.project.meet.application;

import com.mogaco.project.meet.domain.Location;
import com.mogaco.project.meet.domain.Meet;
import com.mogaco.project.meet.domain.MeetTime;
import com.mogaco.project.meet.domain.Message;
import com.mogaco.project.meet.dto.MeetDetailResponseDto;
import com.mogaco.project.meet.dto.MeetJoinDto;
import com.mogaco.project.meet.dto.MeetRequestDto;
import com.mogaco.project.meet.infra.MeetRepository;
import com.mogaco.project.member.domain.Member;
import com.mogaco.project.member.domain.MemberRepository;
import com.mogaco.project.study.domain.Study;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class MeetServiceTest {
    private static final Long GIVEN_MEMBER_ID = 1L;
    private static final Long GIVEN_MEET_ID = 1L;
    private static final String GIVEN_TITLE = "mogaco";
    private static final long NOT_EXISTED_ID = -1L;
    private static final int GIVEN_COUNT = 3;
    private static final String GIVEN_LOCATION = "HONGDAE";
    private static final String GIVEN_LOCATION_DETAIL = "STAR BUCKS";
    private static final String GIVEN_MESSAGE = "LET'S STUDY";
    private static final LocalDate GIVEN_START_DAY = LocalDate.of(2021, 4, 1);
    private static final String GIVEN_START_TIME = "10:00~14:00";
    private static final String APPLY_TIME = "10:00 ~ 14:00";
    private static final String STUDY_SUBJECT = "SPRING";

    private MeetService meetService;
    private MeetRepository meetRepository = mock(MeetRepository.class);
    private MemberRepository memberRepository = mock(MemberRepository.class);

    final Location location = new Location(GIVEN_LOCATION, "STAR_BUCKS");
    final Message givenMessage = new Message(GIVEN_TITLE, GIVEN_MESSAGE);
    final MeetTime givenMeetTime = new MeetTime(GIVEN_START_DAY, GIVEN_START_TIME);
    final List<Study> studies = new ArrayList<>();

    private Meet givenMeet = Meet.builder()
            .id(GIVEN_MEET_ID)
            .message(givenMessage)
            .meetTime(givenMeetTime)
            .location(location)
            .count(GIVEN_COUNT)
            .studies(studies)
            .build();

    @BeforeEach
    void setUp() {
        LocationConverter locationConverter = new LocationConverter();
        meetService = new MeetService(meetRepository, locationConverter, memberRepository);
    }

    @Nested
    @DisplayName("createMeeting 메서드는")
    class Describe_createMeeting {

        @Nested
        @DisplayName("모임 생성 명세서가 주어지면")
        class Context_with_meeting_request {
            final MeetRequestDto meetRequestDto = MeetRequestDto.builder()
                    .count(5)
                    .location("seoul/ hongdae")
                    .title("mogaco")
                    .message("study")
                    .time("10:00~14:00")
                    .subject("DevOps")
                    .startedAt(LocalDate.of(2021, 4, 1))
                    .build();
            final Member member = Member.builder()
                    .id(GIVEN_MEMBER_ID).build();

            @BeforeEach
            void setUp() {
                given(meetRepository.save(any(Meet.class))).will(invocation -> {
                    final Meet source = invocation.getArgument(0);
                    return Meet.builder()
                            .id(1L)
                            .location(source.getLocation())
                            .message(source.getMessage())
                            .count(source.getCount())
                            .meetTime(source.getMeetTime())
                            .build();
                });
                given(memberRepository.findById(anyLong()))
                        .willReturn(Optional.of(member));
            }

            @DisplayName("모임 식별자를 리턴한다.")
            @Test
            void it_returns_id() {
                Long meetId = meetService.createMeeting(meetRequestDto, GIVEN_MEMBER_ID);
                assertThat(meetId).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("getMeeting 메서드는")
    class Describe_getMeeting {

        @Nested
        @DisplayName("등록된 모임 id가 주어지면")
        class Context_with_meet_id {
            final Long meetId = GIVEN_MEET_ID;
            final Meet meet = givenMeet;
            @BeforeEach
            void setUp() {
                given(meetRepository.findById(eq(meetId)))
                        .willReturn(Optional.of(meet));
            }

            @DisplayName("모임 정보를 리턴한다.")
            @Test
            void it_returns_meet_response() {
                MeetDetailResponseDto meeting = meetService.getMeeting(meetId);
                assertThat(meeting.getCount()).isEqualTo(GIVEN_COUNT);
                assertThat(meeting.getTitle()).isEqualTo(GIVEN_TITLE);
                assertThat(meeting.getLocation()).isEqualTo(GIVEN_LOCATION);
            }
        }

        @Nested
        @DisplayName("존재하지 않은 회원 id가 주어지면")
        class Context_with_not_exist_meet {
            final Long meetId = NOT_EXISTED_ID;

            @DisplayName("MeetingNotFoundException 예외를 던진다.")
            @Test
            void it_throws_meeting_not_found_exception() {
                assertThrows(MeetingNotFoundException.class, () -> meetService.getMeeting(meetId));
            }
        }
    }

    @Nested
    @DisplayName("joinMeeting 메서드는")
    class Describe_joinMeeting {

        @Nested
        @DisplayName("등록된 모임 id와 모임 참가 명세서 주어지면")
        class Context_with_meet_id {
            final Long meetId = GIVEN_MEET_ID;
            final MeetJoinDto meetJoinDto = new MeetJoinDto(GIVEN_MEET_ID, APPLY_TIME, STUDY_SUBJECT);
            final Member member = Member.builder()
                    .id(GIVEN_MEMBER_ID).build();
            final Meet meet = givenMeet;

            @BeforeEach
            void setUp() {
                given(meetRepository.findById(eq(meetId)))
                        .willReturn(Optional.of(meet));
                given(memberRepository.findById(anyLong()))
                        .willReturn(Optional.of(member));
            }

            @DisplayName("모임에 공부 참여 정보가 생성된다")
            @Test
            void it_returns_meet_response() {
                final MeetDetailResponseDto meeting = meetService.joinMeeting(GIVEN_MEET_ID, GIVEN_MEMBER_ID, meetJoinDto);

                assertThat(meeting.getStudies().size()).isEqualTo(1);
                assertThat(meeting.getStudies()).extracting("subject").contains(STUDY_SUBJECT);
            }
        }

        @Nested
        @DisplayName("존재하지 않은 회원 id가 주어지면")
        class Context_with_not_exist_meet {
            final Long meetId = NOT_EXISTED_ID;

            @DisplayName("MeetingNotFoundException 예외를 던진다.")
            @Test
            void it_throws_meeting_not_found_exception() {
                assertThrows(MeetingNotFoundException.class, () -> meetService.getMeeting(meetId));
            }
        }
    }
}
