package org.ex9.contractorservice.exception;

/**
 * Исключение, выбрасываемое при отсутствии промышленности в базе данных или её неактивном статусе.
 * Используется в операциях, связанных с получением или удалением промышленности по идентификатору.
 * @author Краковцев Артём
 */
public class OrgFormNotFoundException extends RuntimeException {

    /**
     * Создаёт новое исключение с указанным сообщением.
     *
     * @param message сообщение, описывающее причину исключения
     */
    public OrgFormNotFoundException(String message) {
      super(message);
    }

}
