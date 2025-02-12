package com.example.develop.comment.repository;

import com.example.develop.comment.entity.Comment;
import com.example.develop.comment.dto.CommentCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select new com.example.develop.comment.dto.CommentCountDto(c.schedule.id, count(c)) " +
            "from Comment c " +
            "where c.schedule.id in :scheduleIds " +
            "group by c.schedule.id")
    List<CommentCountDto> countByScheduleIds(List<Long> scheduleIds);

    List<Comment> findByScheduleId(Long scheduleId);
}
