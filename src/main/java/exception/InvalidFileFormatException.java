package exception;

public class InvalidFileFormatException extends Exception {

    private final int lineNumber;

    public InvalidFileFormatException(int lineNumber, String line) {
        super("Неверный формат словаря в строке " + lineNumber + ": «" + line
                + "». Ожидается: слово или выражение | перевод");
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}