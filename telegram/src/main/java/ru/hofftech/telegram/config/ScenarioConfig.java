package ru.hofftech.telegram.config;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurerAdapter;
import io.github.drednote.telegram.handler.scenario.configurer.config.ScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.state.ScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Configuration;
import ru.hofftech.telegram.model.enums.State;

/**
 * Конфигурация сценариев Telegram бота.
 * <p>
 * Настраивает начальное состояние и персистентность для сценариев.
 */
@Configuration
@RequiredArgsConstructor
@NullMarked
public class ScenarioConfig extends ScenarioConfigurerAdapter<State> {

    /**
     * {@inheritDoc}
     * <p>
     * Конфигурация переходов сценария вынесена в properties файлы.
     */
    @Override
    public void onConfigure(ScenarioTransitionConfigurer<State> configurer) {
        // empty because all configuration will be in properties
    }

    /**
     * {@inheritDoc}
     * <p>
     * Отключает персистентность сценариев (используется хранение в памяти).
     */
    @Override
    public void onConfigure(ScenarioConfigConfigurer<State> configurer) {
        configurer.withPersister(null);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Устанавливает начальное состояние для всех сценариев.
     */
    @Override
    public void onConfigure(ScenarioStateConfigurer<State> configurer) throws Exception {
        configurer.withStates().initial(State.INITIAL);
    }
}
