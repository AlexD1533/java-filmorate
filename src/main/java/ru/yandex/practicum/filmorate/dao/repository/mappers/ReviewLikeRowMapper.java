package ru.yandex.practicum.filmorate.dao.repository.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewLikeRowMapper implements RowMapper<ReviewLike> {

    @Override
    public ReviewLike mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setReviewId(resultSet.getLong("review_id"));
        reviewLike.setUserId(resultSet.getLong("user_id"));
        reviewLike.setIsLike(resultSet.getBoolean("is_like"));

        if (resultSet.wasNull()) {
            reviewLike.setIsLike(null);
        }

        return reviewLike;
    }
}