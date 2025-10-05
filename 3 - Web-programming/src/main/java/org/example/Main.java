package org.example;

import com.fastcgi.FCGIInterface;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Objects;

public class Main {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) throws IOException {
        FCGIInterface fcgi = new FCGIInterface();

        while (fcgi.FCGIaccept() >= 0) {
            long startTime = System.nanoTime();
            var method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");

            if (method.equals("GET")) {
                String req = FCGIInterface.request.params.getProperty("QUERY_STRING");

                if (!Objects.equals(req, "")) {
                    try {
                        LinkedHashMap<String, String> inp = getValues(req);

                        int x = Integer.parseInt(inp.get("x"));
                        float y = Float.parseFloat(inp.get("y"));
                        float r = Float.parseFloat(inp.get("r"));


                        if (!(validateX(x) && validateY(y) && validateR(r))) {
                            System.out.println(errorResponse("Недопустимые значения параметров"));
                        } else {
                            boolean isInside = hit(x, y, r);
                            String currentTime = LocalDateTime.now().format(formatter);

                            long executionTime = (System.nanoTime() - startTime) / 1000;
                            System.out.println(successResponse(isInside, x, y, r, currentTime, executionTime));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(errorResponse("Неверный числовой формат"));
                    } catch (Exception e) {
                        System.out.println(errorResponse("Неожиданная ошибка: " + e.getMessage()));
                    }
                } else {
                    System.out.println(errorResponse("Отсутствуют параметры запроса"));
                }
            } else {
                System.out.println(errorResponse("Разрешен только метод GET"));
            }
        }
    }




    private static String successResponse(boolean isInside, int x, float y, float r, String currentTime, long executionTime) {

        return String.format(Locale.US,"""
        HTTP/1.1 200 OK\r
        Content-Type: application/json; charset=utf-8\r
        Access-Control-Allow-Origin: *\r
        Connection: close\r
        \r
        {
          "result": %b,
          "x": %d,
          "y": %.2f,
          "r": %.2f,
          "current_time": "%s",
          "execution_time": "%d мкс",
          "error": null
        }
        """, isInside, x, y, r, currentTime, executionTime);
    }

    private static String errorResponse(String errorMessage) {
        return String.format(Locale.US,"""
        HTTP/1.1 400 Bad Request\r
        Content-Type: application/json; charset=utf-8\r
        Access-Control-Allow-Origin: *\r
        Connection: close\r
        \r
        {
          "result": null,
          "x": null,
          "y": null,
          "r": null,
          "current_time": "%s",
          "execution_time": null,
          "error": "%s"
        }
        """, LocalDateTime.now().format(formatter), errorMessage);
    }

    private static LinkedHashMap<String, String> getValues(String input) {
        String args[] = input.split("&");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (String s : args) {
            String[] arg = s.split("=");
            if (arg.length == 2) {
                map.put(arg[0],arg[1]);
            }
        }
        return map;
    }

    private static boolean hit(int x, float y, float r) {
        if (x < 0 && y < 0) {
            return false;
        } else if (x >= 0 && y <= 0) {
            return (x * x + y * y <= r * r);
        } else if (x >= 0 && y >= 0) {
            return (y <= -x + r && x <= r && y <= r);
        } else {
            return (x >= (-r / 2) && y <= r);
        }
    }

    public static boolean validateX(int x) {
        return x >= -5 && x <= 5;
    }

    public static boolean validateY(float y) {
        return y >= -5 && y <= 5;
    }

    public static boolean validateR(float r) {
        return r >= 1 && r <= 4;
    }
}