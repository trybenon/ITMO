package shared.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Объект-ответ сервера клиенту.
 * Сериализуется и отправляется по сети через ObjectOutputStream.
 */
public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Флаг успеха выполнения команды */
    private boolean success;
    /** Читаемое сообщение (например, результат или ошибка) */
    private String message;
    /**
     * Дополнительные данные ответа.
     * Например, для команды SHOW здесь может лежать List<Person>.
     */
    private Object data;

    /* ---------- Конструкторы ---------- */

    /** Пустой конструктор для десериализации */
    public Response() {}

    /** Только сообщение и успех/неудача */
    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /** Сообщение, успех/неудача и дополнительные данные */
    public Response(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /* ---------- Геттеры / Сеттеры ---------- */

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
