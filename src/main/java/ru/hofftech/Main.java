package ru.hofftech;

import lombok.extern.slf4j.Slf4j;
import ru.hofftech.controller.ConsoleController;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("Стартуем приложение...");
        Main.start();
    }

    private static void start() {
        ConsoleController consoleController = new ConsoleController();
        consoleController.listen();
    }
}
