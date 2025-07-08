package org.ex9.contractorservice.exception;

/**
 * Исключение, выбрасываемое при отсутствии организационной формы в базе данных или её неактивном статусе.
 * Используется в операциях, связанных с получением или удалением организационной формы по идентификатору.
 * @author Краковцев Артём
 */
public class IndustryNotFoundException extends RuntimeException {

    /**
     * Создаёт новое исключение с указанным сообщением.
     *
     * @param message сообщение, описывающее причину исключения
     */
    public IndustryNotFoundException(String message) {
      super(message);
    }

}
