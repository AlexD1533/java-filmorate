package ru.yandex.practicum.filmorate.dao.dto.director;

import ru.yandex.practicum.filmorate.model.Director;
import org.springframework.stereotype.Component;

@Component
public class DirectorMapper {

    public DirectorDto toDto(Director director) {
        if (director == null) {
            return null;
        }

        DirectorDto dto = new DirectorDto();
        dto.setId(director.getId());
        dto.setName(director.getName());
        return dto;
    }

    public Director toEntity(DirectorDto dto) {
        if (dto == null) {
            return null;
        }

        Director director = new Director();
        director.setId(dto.getId());
        director.setName(dto.getName());
        return director;
    }

    public Director toEntity(NewDirectorRequest request) {
        if (request == null) {
            return null;
        }

        Director director = new Director();
        director.setName(request.getName());
        return director;
    }

    public Director updateDirectorFields(Director director, UpdateDirectorRequest request) {
        if (request == null) {
            return director;
        }

        if (request.hasName()) {
            director.setName(request.getName());
        }
        return director;
    }
}