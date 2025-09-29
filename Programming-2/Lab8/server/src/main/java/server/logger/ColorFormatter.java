package server.logger;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Кастомный форматтер для цветного вывода сообщений логгера в консоль.
 * Применяет ANSI-коды для разных уровней логирования.
 */
public class ColorFormatter extends Formatter {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    /**
     * Форматирует запись лога, добавляя цвет в зависимости от уровня логирования.
     *
     * @param record запись лога
     * @return отформатированная строка с ANSI-кодами для консоли
     */
    @Override
    public String format(LogRecord record) {
        String color;
        Level level = record.getLevel();

        // Выбор цвета в зависимости от уровня
        if (level == Level.SEVERE) {
            color = ANSI_RED;
        } else if (level == Level.WARNING) {
            color = ANSI_YELLOW;
        } else if (level == Level.INFO) {
            color = ANSI_GREEN;
        } else if (level == Level.FINE || level == Level.FINER || level == Level.FINEST) {
            color = ANSI_BLUE;
        } else {
            color = "";
        }

        // Формирование сообщения
        StringBuilder sb = new StringBuilder();
        sb.append(color);
        sb.append(new Date(record.getMillis())).append(" ");
        sb.append("[SERVER] ").append(level.getName()).append(": ");
        sb.append(formatMessage(record));
        sb.append(ANSI_RESET);
        sb.append("\n");

        return sb.toString();
    }
}