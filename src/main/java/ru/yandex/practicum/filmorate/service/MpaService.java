package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.GenreMapper;
import ru.yandex.practicum.filmorate.dao.dto.MpaMapper;
import ru.yandex.practicum.filmorate.dao.repository.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.GenreDto;
import ru.yandex.practicum.filmorate.model.MpaDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
   private final MpaRepository mpaRepository;

    public List<MpaDto> getMpa() {
        return mpaRepository.findAll().stream()
                .map(MpaMapper::mapToMpaDto)
                .toList();
    }

    public MpaDto getMpaById(long mpaId) {
        return mpaRepository.findById(mpaId)
                .map(MpaMapper::mapToMpaDto)
                .orElseThrow(() -> new NotFoundException("Рейтинг не найден с ID: " + genreId));


    }


}
