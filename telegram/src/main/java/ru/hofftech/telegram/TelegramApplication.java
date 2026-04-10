package ru.hofftech.telegram;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Главный класс приложения Telegram бота.
 * <p>
 * Точка входа для запуска Spring Boot приложения Telegram модуля.
 */
@SpringBootApplication
@EnableFeignClients
@NullMarked
public class TelegramApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(TelegramApplication.class, args);
    }
}
