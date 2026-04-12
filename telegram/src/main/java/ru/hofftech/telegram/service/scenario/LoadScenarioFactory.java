package ru.hofftech.telegram.service.scenario;

import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenario;
import io.github.drednote.telegram.handler.scenario.property.TelegramScenarioAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.hofftech.shared.model.common.Response;
import ru.hofftech.shared.model.dto.LoadRequestDto;
import ru.hofftech.shared.model.dto.LoadResponseDto;
import ru.hofftech.shared.model.dto.MachineFormRequestDto;
import ru.hofftech.shared.model.dto.ParcelNameRequestDto;
import ru.hofftech.shared.model.enums.LoadStrategyType;
import ru.hofftech.shared.util.PrintStringUtil;
import ru.hofftech.telegram.exception.FeignException;
import ru.hofftech.telegram.exception.ValidateException;
import ru.hofftech.telegram.model.constant.InformationMessage;
import ru.hofftech.telegram.model.enums.State;
import ru.hofftech.telegram.service.ExternalService;
import ru.hofftech.telegram.util.ActionContextUtil;
import ru.hofftech.telegram.util.MessageUtil;
import ru.hofftech.telegram.validation.InputParametersValidator;

import java.util.List;
import java.util.Map;

/**
 * Фабрика сценариев для загрузки посылок в машины.
 * <p>
 * Реализует многошаговый диалог: ввод идентификатора пользователя,
 * списка машин, списка посылок и выбор стратегии загрузки.
 */
@Slf4j
@TelegramScenario
@RequiredArgsConstructor
@NullMarked
@SuppressWarnings("ClassCanBeRecord")
public class LoadScenarioFactory {
    private final InputParametersValidator inputParametersValidator;
    private final ExternalService externalService;

    /**
     * Начинает процесс загрузки посылок.
     * <p>
     * Очищает предыдущие данные диалога и запрашивает идентификатор пользователя.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с запросом идентификатора пользователя
     */
    @TelegramScenarioAction
    public SendMessage startLoad(ActionContext<?> context) {
        log.info("Начало загрузки машины, chatId={}", ActionContextUtil.getChatId(context));

        // Очищаем старые данные
        ActionContextUtil.getVariables(context).clear();

        return MessageUtil.createSendMessage(
                ActionContextUtil.getChatId(context), State.USER_ID.getDescription(), true);
    }

    /**
     * Сохраняет идентификатор пользователя и запрашивает список машин.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с запросом списка машин
     */
    @TelegramScenarioAction
    public SendMessage saveUserId(ActionContext<?> context) {
        String userId = ActionContextUtil.getText(context);

        inputParametersValidator.validateUserId(userId);

        ActionContextUtil.getVariables(context).put(State.USER_ID, userId.trim());

        log.info("Сохранено название: {}, variables={}", userId, ActionContextUtil.getVariables(context));
        return MessageUtil.createSendMessage(
                ActionContextUtil.getChatId(context), State.MACHINES.getDescription(), true);
    }

    /**
     * Сохраняет список машин и запрашивает список посылок.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с запросом списка посылок
     */
    @TelegramScenarioAction
    public SendMessage saveMachines(ActionContext<?> context) {
        String machines = ActionContextUtil.getText(context);
        List<String> machineList = transformTextToStringList(machines);

        if (machineList.isEmpty()) {
            throw new ValidateException("Не найдено ни одной машины для загрузки");
        }

        ActionContextUtil.getVariables(context).put(State.MACHINES, machines);

        log.info("Сохранен список машин: {}, variables={}", machines, ActionContextUtil.getVariables(context));
        return MessageUtil.createSendMessage(
                ActionContextUtil.getChatId(context), State.PARCELS.getDescription(), true);
    }

    /**
     * Сохраняет список посылок и запрашивает стратегию загрузки.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с запросом идентификатора стратегии
     */
    @TelegramScenarioAction
    public SendMessage saveParcels(ActionContext<?> context) {
        String parcels = ActionContextUtil.getText(context);
        List<String> parcelList = transformTextToStringList(parcels);

        if (parcelList.isEmpty()) {
            throw new ValidateException("Не найдено ни одной посылки для загрузки");
        }

        ActionContextUtil.getVariables(context).put(State.PARCELS, parcels);

        log.info(
                "Сохранен список названий посылок: {}, variables={}", parcels, ActionContextUtil.getVariables(context));
        return MessageUtil.createSendMessage(
                ActionContextUtil.getChatId(context), State.STRATEGY.getDescription(), true);
    }

    /**
     * Выполняет загрузку посылок в машины.
     * <p>
     * На основе сохранённых данных формирует запрос к Core сервису.
     *
     * @param context контекст выполнения сценария
     * @return сообщение с результатом загрузки
     * @throws ValidateException если стратегия не найдена
     * @throws FeignException если при вызове Core сервиса произошла ошибка
     */
    @TelegramScenarioAction
    public SendMessage load(ActionContext<?> context) {
        String strategyId = ActionContextUtil.getText(context);

        LoadStrategyType loadStrategyType = LoadStrategyType.fromName(strategyId);

        if (loadStrategyType == null) {
            throw new ValidateException("Стратегия для загрузки %s не найдена. Доступные стратегии: %s"
                    .formatted(strategyId, LoadStrategyType.allStrategies(", ")));
        }

        Map<Object, Object> variables = ActionContextUtil.getVariables(context);
        String userId = (String) variables.get(State.USER_ID);
        String machines = (String) variables.get(State.MACHINES);
        String parcels = (String) variables.get(State.PARCELS);

        if (machines == null || parcels == null) {
            log.error("Отсутствуют обязательные данные: userId, machineList, parcelList");
            return MessageUtil.createSendMessage(
                    ActionContextUtil.getChatId(context), InformationMessage.CLEAR_DIALOG_MESSAGE, false);
        }
        List<MachineFormRequestDto> machineList = transformTextToStringList(machines).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(MachineFormRequestDto::new)
                .toList();

        List<ParcelNameRequestDto> parcelNames = transformTextToStringList(parcels).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(ParcelNameRequestDto::new)
                .toList();

        log.info(
                "Загрузка посылок в машину: userId={}, parcelNames={}, machineList={}, strategyId={}",
                userId,
                parcelNames,
                machineList,
                strategyId);

        LoadRequestDto loadRequestDto = LoadRequestDto.builder()
                .parcels(parcelNames)
                .machines(machineList)
                .userId(userId)
                .loadStrategy(loadStrategyType)
                .build();

        Response<LoadResponseDto> response = externalService.loadParcel(loadRequestDto);

        if (response.isSuccess()) {
            return MessageUtil.createSendMessage(
                    ActionContextUtil.getChatId(context),
                    PrintStringUtil.renderLoadResponse(response.getData()),
                    false);
        }

        throw new FeignException(InformationMessage.FEIGN_ERROR.formatted(
                response.getResult().getMessage(), response.getResult().getCode()));
    }

    /**
     * Преобразует строку с разделителями \n в список строк.
     *
     * @param inputString исходная строка
     * @return список строк
     */
    public List<String> transformTextToStringList(String inputString) {

        String normalizedTxtName = inputString.replace("\\n", "\n");
        return normalizedTxtName.lines().toList();
    }
}
