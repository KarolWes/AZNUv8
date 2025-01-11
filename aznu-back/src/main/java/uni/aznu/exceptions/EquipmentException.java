package uni.aznu.exceptions;

public class EquipmentException extends Exception {
    public EquipmentException() {
    }

    public EquipmentException(String message) {
        super(message);
    }

    public EquipmentException(Throwable cause) {
        super(cause);
    }

    public EquipmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public EquipmentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
