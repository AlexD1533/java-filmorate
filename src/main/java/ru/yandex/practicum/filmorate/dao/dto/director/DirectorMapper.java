package ru.yandex.practicum.filmorate.dao.dto.director;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectorMapper {
    public static Director mapToDirector(DirectorDto request) {
        Director director = new Director();
        director.setName(request.getName());
        return director;
    }

    public DirectorDto mapToDirectorDto(Director director) {
        DirectorDto dto = new DirectorDto();
        dto.setId(director.getId());
        dto.setName(director.getName());
        return dto;
    }
}
