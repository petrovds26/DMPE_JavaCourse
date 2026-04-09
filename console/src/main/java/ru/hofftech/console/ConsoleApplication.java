package ru.hofftech.console;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.shell.command.annotation.CommandScan;

/**
 * Главный класс приложения Console модуля.
 * <p>
 * Точка входа для запуска Spring Shell приложения,
 * предоставляющего консольный интерфейс для управления посылками,
 * загрузкой/разгрузкой машин и просмотра биллинга.
 */
@EnableFeignClients
@SpringBootApplication
@CommandScan
@NullMarked
public class ConsoleApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(ConsoleApplication.class, args);
    }
}
