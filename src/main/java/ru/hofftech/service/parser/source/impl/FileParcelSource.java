package ru.hofftech.service.parser.source.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.hofftech.service.parser.source.ParcelSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Источник данных из файла
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class FileParcelSource implements ParcelSource {

    private final String filePath;

    @Override
    public List<List<String>> getParcelBlocks() throws IOException {
        log.debug("Чтение файла: {}", filePath);

        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IOException("Файл не найден: " + filePath);
        }

        List<String> allLines = Files.readAllLines(path);
        List<List<String>> blocks = new ArrayList<>();
        List<String> currentBlock = new ArrayList<>();

        for (String line : allLines) {
            if (line.trim().isEmpty()) {
                if (!currentBlock.isEmpty()) {
                    blocks.add(new ArrayList<>(currentBlock));
                    currentBlock.clear();
                }
            } else {
                currentBlock.add(line);
            }
        }

        // Добавляем последний блок
        if (!currentBlock.isEmpty()) {
            blocks.add(currentBlock);
        }

        log.debug("Файл прочитан. Найдено блоков (посылок): {}", blocks.size());
        return blocks;
    }

    @Override
    public String getDescription() {
        return "Файл: " + filePath;
    }
}
