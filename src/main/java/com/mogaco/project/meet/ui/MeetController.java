package com.mogaco.project.meet.ui;

import com.mogaco.project.auth.application.LoginNotFoundException;
import com.mogaco.project.global.utils.SecurityUtil;
import com.mogaco.project.meet.application.MeetService;
import com.mogaco.project.meet.dto.MainResponseDto;
import com.mogaco.project.meet.dto.MeetDetailResponseDto;
import com.mogaco.project.meet.dto.MeetJoinDto;
import com.mogaco.project.meet.dto.MeetRequestDto;
import com.mogaco.project.meet.dto.MyMeetResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * 모임의 사용자 요청을 처리한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meets")
public class MeetController {
    private final MeetService meetService;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<Long> createMeeting(@RequestBody @Valid MeetRequestDto dto) {
        final Long loginMemberId = securityUtil.getCurrentMemberId()
                .orElseThrow(LoginNotFoundException::new);
        final Long meetId = meetService.createMeeting(dto, loginMemberId);

        return ResponseEntity.created(URI.create("/meets/" + meetId))
                .body(meetId);
    }

    @GetMapping
    public Page<MainResponseDto> getMeetingsByPageRequest(final Pageable pageable) {
        return meetService.getMeetingsByPageRequest(pageable);
    }

    /**
     * 주어진 식별자에 해당하는 모임을 리턴한다.
     */
    @GetMapping("{id}")
    public ResponseEntity<MeetDetailResponseDto> getMeeting(@PathVariable Long id) {
        final MeetDetailResponseDto meetResponse = meetService.getMeeting(id);
        return ResponseEntity.ok().body(meetResponse);
    }

    /**
     * 주어진 식별자에 해당하는 모임에 참가한다.
     */
    @PostMapping("{id}/join")
    public ResponseEntity<MeetDetailResponseDto> joinMeeting(@PathVariable Long id, @Valid @RequestBody MeetJoinDto meetJoinDto) {
        final Long loginMemberId = securityUtil.getCurrentMemberId()
                .orElseThrow(LoginNotFoundException::new);
        final MeetDetailResponseDto meetResponse = meetService.joinMeeting(id, loginMemberId, meetJoinDto);

        return ResponseEntity.created(URI.create("/meets/" + id))
                .body(meetResponse);
    }

    /**
     * 회원의 모임 참가 목록을 가져옵니다.
     */
    @GetMapping("/join/{id}")
    public ResponseEntity<List<MyMeetResponseDto>> getJoinMeetings(@PathVariable Long id) {
        List<MyMeetResponseDto> myMeets = meetService.getJoinMeetings(id);
        return ResponseEntity.ok().body(myMeets);
    }

    /**
     * 모임을 삭제합니다.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteMeeting(@PathVariable Long id) {
        meetService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }
}
