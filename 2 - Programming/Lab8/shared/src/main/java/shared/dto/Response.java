package shared.dto;

import shared.model.Info;
import shared.model.Person;


import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Объект-ответ сервера клиенту.
 * Сериализуется и отправляется по сети через ObjectOutputStream.
 */
public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private  Info info;
    private CommandType respType;

    /** Статус выполнения команды */
    private ResponseStatus respStatus;
    /** Читаемое сообщение (например, результат или ошибка) */
    private String message;
    /**
     * Дополнительные данные ответа.
     * Например, для команды SHOW здесь может лежать List<Person>.
     */
    private Object data;

    private User user;

    private LinkedList<Person> people;

    private ArrayList<Response> responseList = new ArrayList<>();

    private ArrayList<String> desk = new ArrayList<>();

    /* ---------- Конструкторы ---------- */

    /** Пустой конструктор для десериализации */
    public Response() {

    }

    public Response(ResponseStatus respStatus, String message, CommandType respType, LinkedList<Person> people) {
        this.respStatus = respStatus;
        this.message = message;
        this.respType = respType;
        this.people = people;
    }

    public Response(ResponseStatus respStatus, String message, CommandType respType, User user, LinkedList<Person> people) {
        this.respStatus = respStatus;
        this.message = message;
        this.respType = respType;
        this.user = user;
        this.people = people;
    }

    public LinkedList<Person> getPersons() {
        return people;
    }



    public Response(ResponseStatus respStatus, String message, CommandType respType) {
        this.respStatus = respStatus;
        this.message = message;
        this.respType = respType;

    }
    public Response(ResponseStatus respStatus, Info info, CommandType respType) {
        this.respStatus = respStatus;
        this.info = info;
        this.respType = respType;

    }
    public Response(ResponseStatus respStatus, String message) {
        this.respStatus = respStatus;
        this.message = message;
    }

    public Response(ResponseStatus respStatus, ArrayList<String> desk, CommandType respType) {
        this.respStatus = respStatus;
        this.desk = desk;
        this.respType = respType;
    }

        public void setUser (User user){
            this.user = user;
        }

        public User getUser () {
            return user;
        }

    public Response( ResponseStatus respStatus,String message,CommandType respType, User user) {
            this.respStatus = respStatus;
            this.message = message;
            this.respType = respType;
            this.user = user;
        }

        /** Сообщение, успех/неудача и дополнительные данные */
    public Response(String message, Object data, CommandType respType, ResponseStatus respStatus) {
            this.respType = respType;
            this.message = message;
            this.data = data;
        }

        /* ---------- Геттеры / Сеттеры ---------- */

        public ResponseStatus getStatus () {
            return respStatus;
        }

        public void setStatus (ResponseStatus respStatus){
            this.respStatus = respStatus;
        }

        public void setType (CommandType respType){
            this.respType = respType;
        }

        public String getMessage () {
            return message;
        }

        public void setMessage (String message){
            this.message = message;
        }

        public Object getData () {
            return data;
        }

        public void setData (Object data){
            this.data = data;
        }

        public Info getInfo() {
        return info;
    }

        public CommandType getType () {
            return respType;
        }

        @Override
        public String toString () {
            return "Response{" +
                    "status=" + respStatus +
                    "type=" + respType +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }

    public ArrayList<String> getCommandCollection() {
            return desk;
    }
}
