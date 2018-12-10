package core.logic_fs;

public class NameTakenException extends Exception
{
    public NameTakenException()
    {
        super();
    }

    public NameTakenException(String message)
    {
        super(message);
    }

    public NameTakenException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public NameTakenException(Throwable cause)
    {
        super(cause);
    }

    protected NameTakenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
