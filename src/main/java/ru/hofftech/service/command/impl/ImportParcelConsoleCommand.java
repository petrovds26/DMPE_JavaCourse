package ru.hofftech.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.hofftech.service.ParcelProcessor;
import ru.hofftech.service.command.ConsoleCommand;
import ru.hofftech.service.command.ConsoleCommandType;
import ru.hofftech.service.loader.strategy.ParcelLoadingStrategy;
import ru.hofftech.service.loader.strategy.impl.DensePackingStrategy;
import ru.hofftech.service.loader.strategy.impl.OneParcelPerMachineStrategy;
import ru.hofftech.service.output.impl.ParcelOutputLog;
import ru.hofftech.service.parser.ParcelBuilder;
import ru.hofftech.service.parser.ParcelNormalizer;
import ru.hofftech.service.parser.source.impl.FileParcelSource;
import ru.hofftech.service.validation.impl.ParcelGridValidator;
import ru.hofftech.service.validation.impl.ParcelListStringValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ImportParcelConsoleCommand implements ConsoleCommand {

    private final Pattern pattern = Pattern.compile(ConsoleCommandType.IMPORT + "\\s+(.+?\\.txt)\\s+(\\d+)");
    private final List<ParcelLoadingStrategy> loadingStrategy;
    private final ParcelNormalizer normalizer;
    private final ParcelBuilder parcelBuilder;
    private final ParcelListStringValidator stringValidator;
    private final ParcelGridValidator gridValidator;
    private final ParcelOutputLog parcelOutput;

    public ImportParcelConsoleCommand() {
        this.loadingStrategy = new ArrayList<>();
        loadingStrategy.add(new OneParcelPerMachineStrategy());
        loadingStrategy.add(new DensePackingStrategy());

        this.normalizer = new ParcelNormalizer();
        this.parcelBuilder = new ParcelBuilder();
        this.stringValidator = new ParcelListStringValidator();
        this.gridValidator = new ParcelGridValidator();
        this.parcelOutput = new ParcelOutputLog();
    }

    @Override
    public String getName() {
        return ConsoleCommandType.IMPORT.toString();
    }

    @Override
    public String getDescription() {
        return "Импорт посылок из файла. Формат: import filename.txt [стратегия]. " + "Стратегии: "
                + getStrategiesDescription();
    }

    @Override
    public boolean matches(String input) {
        return pattern.matcher(input.trim()).matches();
    }

    @Override
    public void execute(String input) {
        Matcher matcher = pattern.matcher(input.trim());
        if (matcher.matches()) {
            String filePath = matcher.group(1);
            String strategyParam = matcher.group(2);
            ParcelLoadingStrategy strategy = parseStrategyId(strategyParam);

            processor(filePath, strategy);
        } else {
            log.error(
                    "Неверный формат команды импорта. Используйте: import filename.txt [стратегия]. Доступные стратегии: {}",
                    getStrategiesDescription());
        }
    }

    /**
     * Обработка файла с посылками
     */
    private void processor(String filePath, ParcelLoadingStrategy strategy) {
        if (filePath == null || strategy == null || filePath.isEmpty()) {
            log.error(
                    "Не удалось распарсить команду импорта. Используйте: import filename.txt [стратегия]. Доступные стратегии: {}",
                    getStrategiesDescription());
            return;
        }

        log.debug("Импорт файла: {} с стратегией: {}", filePath, strategy.getAlgorithmName());

        ParcelProcessor processor = new ParcelProcessor(
                new FileParcelSource(filePath),
                normalizer,
                parcelBuilder,
                stringValidator,
                gridValidator,
                strategy,
                parcelOutput);

        processor.process();
    }

    /**
     * Парсит ID стратегии из параметра
     */
    private ParcelLoadingStrategy parseStrategyId(String strategyParam) {
        try {
            int id = Integer.parseInt(strategyParam.trim());
            return getStrategyById(id);
        } catch (NumberFormatException e) {
            log.error("Некорректный ID стратегии '{}'. Ошибка: {}", strategyParam, e.getMessage());
        }

        return null;
    }

    /**
     * Формирует описание доступных стратегий
     */
    private String getStrategiesDescription() {
        return loadingStrategy.stream()
                .map(strategy ->
                        String.format("%d - %s; ", strategy.getAlgorithmType().getId(), strategy.getAlgorithmName()))
                .collect(Collectors.joining());
    }

    /**
     * Получить стратегию по ID
     * @param id идентификатор стратегии
     */
    private ParcelLoadingStrategy getStrategyById(int id) {
        return loadingStrategy.stream()
                .filter(type -> type.getAlgorithmType().getId() == id)
                .findFirst()
                .orElse(null);
    }
}
