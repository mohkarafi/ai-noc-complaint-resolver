package karafi.aicomplaint.common.exception;

import java.io.IOException;

public class GlobalExceptionHandler extends IOException {
    public GlobalExceptionHandler(String message) {
        super(message);
    }
}
