package com.mogaco.project.meet.infra;

import com.mogaco.project.meet.dto.MainResponseDto;
import com.mogaco.project.meet.dto.QMainResponseDto;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.mogaco.project.meet.domain.QMeet.meet;

@RequiredArgsConstructor
public class MeetRepositoryImpl implements MeetRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MainResponseDto> searchMainPage(Pageable pageable) {

        QueryResults<MainResponseDto> result = queryFactory
                .select(new QMainResponseDto(
                        meet.id.as("meetId"),
                        meet.meetTime.startedAt,
                        meet.message.title,
                        meet.location.location,
                        meet.meetTime.time,
                        meet.meetStatus
                ))
                .from(meet)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainResponseDto> content = result.getResults();
        long total = result.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
}
