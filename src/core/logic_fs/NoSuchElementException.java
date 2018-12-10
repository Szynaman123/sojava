package core.logic_fs;

public class NoSuchElementException extends Exception
{
    public NoSuchElementException()
    {
        super();
    }

    public NoSuchElementException(String message)
    {
        super(message);
    }

    public NoSuchElementException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public NoSuchElementException(Throwable cause)
    {
        super(cause);
    }

    protected NoSuchElementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
