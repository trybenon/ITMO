package console;

import model.enums.Color;

/**
 * Класс {@code ReadManager} используется для чтения различных данных с консоли.
 * Он содержит методы для ввода различных типов данных, таких как строка, числа, координаты и т.д.
 * Каждый метод включает в себя обработку ошибок ввода и запрос повторного ввода в случае неверного ввода.
 * Также некоторые поля теперь могут быть {@code null}, если ввод не требуется.
 */
public class ReadManager {
    ConsoleManager consoleManager = new ConsoleManager();

    /**
     * Метод для чтения имени пользователя.
     * Запрашивает ввод до тех пор, пока не будет введено непустое имя.
     *
     * @return имя пользователя
     */
    public String readName() {
        System.out.println("Введите имя: ");
        while (true) {
            String name = consoleManager.readLine();
            if (!name.isBlank()) {
                return name;
            }
            System.out.println("Имя не может быть пустым. Введите снова:");
        }
    }

    /**
     * Метод для чтения координаты X.
     * Может быть {@code null}.
     *
     * @return координата X или {@code null}
     */
    public Long readCoordinateX() {
        System.out.println("Введите координату X (максимум 59) или оставьте пустым: ");
        while (true) {
            String input = consoleManager.readLine();
            if (input.isBlank()) return null;
            try {
                long x = Long.parseLong(input);
                if (x > 59) {
                    System.out.println("Ошибка: X не может быть больше 59. Попробуйте снова.");
                    continue;
                }
                return x;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное целое число или оставьте поле пустым.");
            }
        }
    }

    /**
     * Метод для чтения координаты Y.
     * Может быть {@code null}.
     *
     * @return координата Y или {@code null}
     */
    public Double readCoordinateY() {
        System.out.println("Введите координату Y (максимум 426) или оставьте пустым: ");
        while (true) {
            String input = consoleManager.readLine();
            if (input.isBlank()) return null;
            try {
                double y = Double.parseDouble(input);
                if (y > 426) {
                    System.out.println("Ошибка: Y не может быть больше 426. Попробуйте снова.");
                    continue;
                }
                return y;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число или оставьте поле пустым.");
            }
        }
    }

    /**
     * Метод для чтения координаты X для местоположения.
     * Может быть {@code null}.
     *
     * @return координата X или {@code null}
     */
    public Double readLocationX() {
        System.out.println("Введите координату X для местоположения или оставьте пустым: ");
        while (true) {
            String input = consoleManager.readLine();
            if (input.isBlank()) return null;
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число или оставьте поле пустым.");
            }
        }
    }

    /**
     * Метод для чтения координаты Y для местоположения.
     * Может быть {@code null}.
     *
     * @return координата Y или {@code null}
     */
    public Float readLocationY() {
        System.out.println("Введите координату Y для местоположения или оставьте пустым: ");
        while (true) {
            String input = consoleManager.readLine();
            if (input.isBlank()) return null;
            try {
                return Float.parseFloat(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число или оставьте поле пустым.");
            }
        }
    }

    /**
     * Метод для чтения координаты Z для местоположения.
     * Может быть {@code null}.
     *
     * @return координата Z или {@code null}
     */
    public Integer readLocationZ() {
        System.out.println("Введите координату Z для местоположения: ");
        while (true) {
            String input = consoleManager.readLine();
            if (input.isBlank());
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное целое число.");
            }
        }
    }

    /**
     * Метод для чтения роста (height).
     * Должно быть больше 0.
     *
     * @return рост человека
     */
    public int readHeight() {
        System.out.println("Введите рост (должен быть больше 0):");
        while (true) {
            try {
                int height = consoleManager.readInt();
                if (height > 0) {
                    return height;
                }
                System.out.println("Ошибка: рост должен быть больше 0. Попробуйте снова.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    /**
     * Метод для чтения веса (weight).
     * Должно быть больше 0.
     *
     * @return вес человека
     */
    public long readWeight() {
        System.out.println("Введите вес (должен быть больше 0):");
        while (true) {
            try {
                long weight = consoleManager.readLong();
                if (weight > 0) {
                    return weight;
                }
                System.out.println("Ошибка: вес должен быть больше 0. Попробуйте снова.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    /**
     * Метод для чтения цвета глаз.
     * Может быть {@code null}.
     *
     * @return цвет глаз или {@code null}
     */
    public Color readEyeColor() {
        System.out.println("Введите цвет глаз (RED, BLACK, BLUE, ORANGE, BROWN) или оставьте пустым: ");
        while (true) {
            String input = consoleManager.readLine();
            if (input.isBlank()) return null;
            try {
                return Color.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный цвет глаз. Введите заново или оставьте поле пустым.");
            }
        }
    }

    /**
     * Метод для чтения паспортного ID.
     * Длина должна быть от 6 до 41 символа. Может быть {@code null}.
     *
     * @return паспортный ID или {@code null}
     */
    public String readPassportId() {
        System.out.println("Введите паспортный ID (от 6 до 41 символа) или оставьте пустым:");
        while (true) {
            String passportId = consoleManager.readLine();

            if (passportId == null || passportId.trim().isEmpty()) {
                return null; // Возвращаем null, если пользователь оставил поле пустым
            }

            passportId = passportId.trim(); // Убираем пробелы по краям

            if (passportId.length() >= 6 && passportId.length() <= 41) {
                return passportId;
            }

            System.out.println("Ошибка: паспортный ID должен быть от 6 до 41 символа. Попробуйте снова.");
        }
    }
}
