package org.ex9.contractorservice.exception;

/**
 * Исключение, выбрасываемое при отсутствии страны в базе данных или её неактивном статусе.
 * Используется в операциях, связанных с получением или удалением страны по идентификатору.
 * @author Краковцев Артём
 */
public class CountryNotFoundException extends RuntimeException {

    /**
     * Создаёт новое исключение с указанным сообщением.
     *
     * @param message сообщение, описывающее причину исключения
     */
    public CountryNotFoundException(String message) {

        super(message);

    }

}
