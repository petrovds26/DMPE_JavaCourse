package ru.hofftech.importmachine.service.output.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import ru.hofftech.importmachine.model.core.ImportMachineResult;
import ru.hofftech.importmachine.service.output.ImportMachineOutput;
import ru.hofftech.shared.model.core.Parcel;
import ru.hofftech.shared.model.enums.FileType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Вывод результата разгрузки машин в текстовый файл
 * Формат: посылки разделены пустыми строками
 */
@Slf4j
@RequiredArgsConstructor
public class ImportMachineOutputTxt implements ImportMachineOutput {

    @Override
    public void output(@NonNull ImportMachineResult result, @Nullable String outputFilePath) {
        if (outputFilePath == null || outputFilePath.isBlank()) {
            log.error("Не указан путь для сохранения текстового файла");
            return;
        }

        try {
            // Формируем текстовое содержимое
            String content = formatParcelsToText(result.parcels());

            // Сохраняем в файл
            Files.writeString(Path.of(outputFilePath), content);

            log.info("Результат успешно сохранён в текстовый файл: {}", outputFilePath);
            log.debug("Сохранено {} посылок", result.parcels().size());

        } catch (IOException e) {
            log.error("Ошибка при сохранении результата в текстовый файл {}: {}", outputFilePath, e.getMessage(), e);
        }
    }

    @Override
    public @NonNull Optional<FileType> getFileTypeOptional() {
        return Optional.of(FileType.TXT);
    }

    @Override
    public @NonNull String getDescription() {
        return FileType.TXT.getDescription();
    }

    /**
     * Форматирует список посылок в текстовый формат
     * Посылки разделяются пустыми строками
     */
    @NonNull
    private String formatParcelsToText(@NonNull List<Parcel> parcels) {
        if (parcels.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parcels.size(); i++) {
            Parcel parcel = parcels.get(i);

            // Добавляем строки посылки
            sb.append(parcel.getForm()).append("\n");

            // Добавляем пустую строку между посылками (кроме последней)
            if (i < parcels.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
