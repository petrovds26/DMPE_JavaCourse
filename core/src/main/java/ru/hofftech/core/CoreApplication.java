package ru.hofftech.core;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Core модуля.
 * <p>
 * Точка входа для запуска Spring Boot приложения,
 * предоставляющего REST API для управления посылками,
 * загрузкой/разгрузкой машин и биллингом.
 */
@NullMarked
@SpringBootApplication
public class CoreApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }
}
