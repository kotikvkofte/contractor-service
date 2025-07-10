package org.ex9.contractorservice.exception;

/**
 * Исключение, выбрасываемое при отсутствии контрагента в базе данных или её неактивном статусе.
 * Используется в операциях, связанных с получением или удалением контрагента по идентификатору.
 * @author Краковцев Артём
 */
public class ContractorNotFoundException extends RuntimeException {

    /**
     * Создаёт новое исключение с указанным сообщением.
     *
     * @param message сообщение, описывающее причину исключения
     */
    public ContractorNotFoundException(String message) {

        super(message);

    }

}
