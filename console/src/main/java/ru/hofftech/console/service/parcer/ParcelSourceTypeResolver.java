package ru.hofftech.console.service.parcer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import ru.hofftech.console.exception.ValidateException;
import ru.hofftech.console.model.enums.LoadInputParcelType;
import ru.hofftech.console.util.FileTypeUtil;
import ru.hofftech.console.validation.impl.InputFilePathValidator;
import ru.hofftech.shared.model.enums.FileType;

import java.util.List;

/**
 * Сервис для определения типа источника данных при парсинге посылок.
 * <p>
 * Анализирует переданные параметры (файл или текст) и определяет,
 * какой тип источника следует использовать для парсинга.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@NullMarked
public class ParcelSourceTypeResolver {

    private final InputFilePathValidator inputFilePathValidator;

    /**
     * Определяет тип источника данных на основе переданных параметров.
     *
     * @param inputParcelFile путь к файлу (может быть пустым)
     * @param inputParcelText текст с посылками (может быть пустым)
     * @return тип источника данных
     * @throws ValidateException если ни один параметр не заполнен, или оба заполнены,
     *                           или файл имеет неподдерживаемый тип
     */
    public LoadInputParcelType resolve(String inputParcelFile, String inputParcelText) {
        if (inputParcelFile.isBlank() && inputParcelText.isBlank()) {
            throw new ValidateException("Необходимо указать либо --parcelsFile, либо --parcelsText");
        }

        // Если заполнен текст
        if (!inputParcelText.isBlank()) {
            log.debug("Определён тип источника: TEXT");
            return LoadInputParcelType.TEXT;
        }

        // Если заполнен файл - определяем тип по расширению
        return resolveFileType(inputParcelFile);
    }

    /**
     * Определяет тип источника по расширению файла.
     *
     * @param filePath путь к файлу
     * @return тип источника для файла
     * @throws ValidateException если файл не проходит валидацию или имеет неподдерживаемый тип
     */
    private LoadInputParcelType resolveFileType(String filePath) {
        // Валидируем файл
        List<String> errors = inputFilePathValidator.validate(filePath);
        if (!errors.isEmpty()) {
            throw new ValidateException("Ошибка валидации файла: " + String.join("; ", errors));
        }

        // Определяем тип файла по расширению
        FileType fileType = FileTypeUtil.fromFilename(filePath);
        if (fileType == null) {
            throw new ValidateException(
                    "Не удалось определить тип файла: " + filePath + ". Поддерживаемые типы: .txt, .json");
        }

        LoadInputParcelType result = LoadInputParcelType.fileType2LoadInputParcelType(fileType);
        if (result == null) {
            throw new ValidateException("Тип файла " + fileType + " не поддерживается для загрузки посылок");
        }

        log.debug("Определён тип источника: {} для файла {}", result, filePath);
        return result;
    }
}
