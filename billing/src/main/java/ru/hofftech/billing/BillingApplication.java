package ru.hofftech.billing;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Billing модуля.
 * <p>
 * Точка входа для запуска Spring Boot приложения,
 * предоставляющего REST API для управления биллингом
 * и consuming сообщений из Kafka.
 */
@NullMarked
@SpringBootApplication
public class BillingApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(BillingApplication.class, args);
    }
}
