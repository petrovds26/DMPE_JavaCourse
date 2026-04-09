package ru.hofftech.core.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import ru.hofftech.core.exception.ParcelException;
import ru.hofftech.core.mapper.ParcelEntityMapper;
import ru.hofftech.core.model.core.Parcel;
import ru.hofftech.core.model.entity.ParcelEntity;
import ru.hofftech.core.repository.ParcelRepository;
import ru.hofftech.shared.model.dto.ParcelDto;
import ru.hofftech.shared.model.dto.newdto.ParcelFormRequestDto;
import ru.hofftech.shared.model.dto.newdto.ParcelNameRequestDto;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для CRUD операций с посылками.
 * <p>
 * Предоставляет методы для создания, чтения, обновления и удаления посылок.
 */
@Service
@RequiredArgsConstructor
@NullMarked
public class ParcelService {
    private final ParcelRepository parcelRepository;
    private final ParcelEntityMapper parcelEntityMapper;

    /**
     * Создаёт новую посылку.
     *
     * @param parcelFormRequestDto DTO с данными посылки
     * @return сообщение о результате операции
     * @throws ParcelException если посылка с таким названием уже существует
     */
    public String create(ParcelFormRequestDto parcelFormRequestDto) {

        Parcel parcel = parcelEntityMapper.toParcel(parcelFormRequestDto);

        if (parcelRepository.findByName(parcel.name()).isPresent()) {
            throw new ParcelException(
                    "Посылка с таким названием уже существует: %s".formatted(parcelFormRequestDto.name()));
        }
        parcelRepository.save(parcelEntityMapper.toParcelEntity(parcel));

        return "Создана посылка. Название: %s".formatted(parcel.getName());
    }

    /**
     * Обновляет существующую посылку.
     *
     * @param parcelFormRequestDto DTO с обновлёнными данными посылки
     * @return сообщение о результате операции
     * @throws ParcelException если посылка с таким названием не существует
     */
    public String update(ParcelFormRequestDto parcelFormRequestDto) {

        Parcel parcel = parcelEntityMapper.toParcel(parcelFormRequestDto);

        Optional<ParcelEntity> parcelEntityOptional = parcelRepository.findByName(parcel.name());

        if (parcelEntityOptional.isEmpty()) {
            throw new ParcelException(
                    "Посылка с таким названием не существует: %s".formatted(parcelFormRequestDto.name()));
        }
        parcelRepository.save(parcelEntityMapper.toParcelEntity(parcel, parcelEntityOptional.get()));

        return "Обновлена посылка. Название: %s".formatted(parcel.getName());
    }

    /**
     * Удаляет посылку по названию.
     *
     * @param parcelNameRequestDto DTO с названием посылки
     * @return сообщение о результате операции
     * @throws ParcelException если посылка с таким названием не существует
     */
    public String delete(ParcelNameRequestDto parcelNameRequestDto) {

        Optional<ParcelEntity> parcelEntityOptional = parcelRepository.findByName(parcelNameRequestDto.name());
        if (parcelEntityOptional.isEmpty()) {
            throw new ParcelException(
                    String.format("Посылка с названием %s не существует", parcelNameRequestDto.name()));
        }

        parcelRepository.delete(parcelEntityOptional.get());

        return String.format("Удаление посылки %s завершено", parcelNameRequestDto.name());
    }

    /**
     * Возвращает список всех посылок.
     *
     * @return список DTO всех посылок
     */
    public List<ParcelDto> readAll() {
        return parcelRepository.findAll().stream()
                .map(parcelEntityMapper::toDto)
                .toList();
    }

    /**
     * Возвращает посылку по названию.
     *
     * @param name название посылки
     * @return список с одной посылкой
     * @throws ParcelException если посылка не найдена
     */
    public List<ParcelDto> readByName(String name) {
        ParcelEntity entity = parcelRepository
                .findByName(name)
                .orElseThrow(() -> new ParcelException(String.format("Посылка с названием %s не существует", name)));

        return List.of(parcelEntityMapper.toDto(entity));
    }
}
