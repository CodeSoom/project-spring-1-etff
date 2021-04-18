package com.mogaco.project.meet.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogaco.project.global.utils.SecurityUtil;
import com.mogaco.project.meet.application.MeetService;
import com.mogaco.project.meet.domain.MeetStatus;
import com.mogaco.project.meet.dto.MainResponseDto;
import com.mogaco.project.meet.dto.MeetDetailResponseDto;
import com.mogaco.project.meet.dto.MeetJoinDto;
import com.mogaco.project.meet.dto.MeetRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MeetControllerTest {
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTY0MDk2MjgwMCwiZXh" +
            "wIjoxNjQwOTYzMTAwfQ.2siRnBJmRU2JXjZY0CkQMgnCHRJN4Dld4_wG6R7T-HQ";

    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";
    public static final String GIVEN_TITLE = "mogaco";
    public static final long GIVEN_ID = 1L;
    public static final String GIVEN_LOCATION = "HONGDAE";
    public static final LocalDate GIVEN_START_DAY = LocalDate.of(2021, 4, 1);
    public static final String GIVEN_START_TIME = "10:00~14:00";

    @MockBean
    private MeetService meetService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private SecurityUtil securityUtil;

    private MeetRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = MeetRequestDto.builder()
                .count(5)
                .location("seoul/ hongdae")
                .title("mogaco")
                .message("study")
                .time("10:00~14:00")
                .subject("DevOps")
                .startedAt(LocalDate.of(2021, 4, 1))
                .build();
        given(securityUtil.getCurrentMemberId()).willReturn(Optional.of(1L));

        given(meetService.createMeeting(any(MeetRequestDto.class), anyLong()))
                .willReturn(1L);

        PageRequest pageRequest = PageRequest.of(0, 20);
        List<MainResponseDto> result = List.of(MainResponseDto.builder()
                .title(GIVEN_TITLE)
                .meetId(GIVEN_ID)
                .location(GIVEN_LOCATION)
                .startedAt(GIVEN_START_DAY)
                .time(GIVEN_START_TIME)
                .status(MeetStatus.OPEN)
                .build());

        Page<MainResponseDto> page = new PageImpl<>(result, pageRequest, result.size());
        given(meetService.getMeetingsByPageRequest(any(PageRequest.class))).willReturn(page);
    }

    @Test
    void createWithValidAttributes() throws Exception {
        mockMvc.perform(
                post("/api/v1/meets")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto))
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(header().string("location", "/meets/1"))
                .andExpect(status().isCreated());

        verify(meetService).createMeeting(any(MeetRequestDto.class), anyLong());
    }

    @Test
    void createWithInvalidAttributes() throws Exception {
        final MeetRequestDto dto = MeetRequestDto.builder()
                .build();

        mockMvc.perform(
                post("/api/v1/meets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithoutAccessToken() throws Exception {
        mockMvc.perform(
                post("/api/v1/meets")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto))

        )
                .andExpect(status().isUnauthorized());
    }


    @Test
    void list() throws Exception {
        mockMvc.perform(
                get("/api/v1/meets")
        )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(GIVEN_TITLE)))
                .andExpect(content().string(containsString(GIVEN_LOCATION)));
    }

    @Test
    void getMeeting() throws Exception {
        MeetDetailResponseDto responseDto = MeetDetailResponseDto.builder()
                .meetId(GIVEN_ID)
                .startedAt(GIVEN_START_DAY)
                .title(GIVEN_TITLE)
                .location(GIVEN_LOCATION)
                .time(GIVEN_START_TIME)
                .studies(new ArrayList())
                .build();

        given(meetService.getMeeting(anyLong()))
                .willReturn(responseDto);

        mockMvc.perform(
                get("/api/v1/meets/{id}", GIVEN_ID)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(GIVEN_TITLE)))
                .andExpect(content().string(containsString(GIVEN_LOCATION)));
    }

    @Test
    void joinMeetingWithValidAttributes() throws Exception {
        final MeetJoinDto meetJoinDto = new MeetJoinDto(GIVEN_ID, "10:00 ~ 14:00", "DDD");

        mockMvc.perform(
                post("/api/v1/meets/{id}/join", GIVEN_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(meetJoinDto))
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(header().string("location", "/meets/1"))
                .andExpect(status().isCreated());

        verify(meetService).joinMeeting(anyLong(), anyLong(), any(MeetJoinDto.class));
    }

    @Test
    void joinMeetingWithInValidAttributes() throws Exception {
        final MeetJoinDto meetJoinDto = new MeetJoinDto();

        mockMvc.perform(
                post("/api/v1/meets/{id}/join", GIVEN_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(meetJoinDto))
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void joinMeetingWithoutAccessToken() throws Exception {
        final MeetJoinDto meetJoinDto = new MeetJoinDto(GIVEN_ID, "10:00 ~ 14:00", "DDD");

        mockMvc.perform(
                post("/api/v1/meets/{id}/join", GIVEN_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(meetJoinDto))
        )
                .andExpect(status().isUnauthorized());
    }
}
