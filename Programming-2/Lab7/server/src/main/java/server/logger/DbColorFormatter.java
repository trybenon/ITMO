package server.logger;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * Кастомный форматтер для цветного вывода сообщений логгера базы данных в консоль.
 * Использует уникальные ANSI-коды для различения от основного логгера.
 */
public class DbColorFormatter extends Formatter {
    // ANSI-коды для цветов
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_PURPLE = "\u001B[35m"; // Фиолетовый для INFO
    private static final String ANSI_CYAN = "\u001B[36m";   // Циан для WARNING
    private static final String ANSI_WHITE_ON_RED = "\u001B[37;41m"; // Белый на красном для SEVERE
    private static final String ANSI_GRAY = "\u001B[90m";   // Серый для FINE/FINER/FINEST

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
            color = ANSI_WHITE_ON_RED;
        } else if (level == Level.WARNING) {
            color = ANSI_CYAN;
        } else if (level == Level.INFO) {
            color = ANSI_PURPLE;
        } else if (level == Level.FINE || level == Level.FINER || level == Level.FINEST) {
            color = ANSI_GRAY;
        } else {
            color = "";
        }

        // Формирование сообщения
        StringBuilder sb = new StringBuilder();
        sb.append(color);
        sb.append(new Date(record.getMillis())).append(" ");
        sb.append("[DB] ").append(level.getName()).append(": ");
        sb.append(formatMessage(record));
        sb.append(ANSI_RESET);
        sb.append("\n");

        return sb.toString();
    }
}