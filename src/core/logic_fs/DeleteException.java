package core.logic_fs;

public class DeleteException extends Exception
{

    public DeleteException()
    {
        super();
    }

    public DeleteException(String message)
    {
        super(message);
    }

    public DeleteException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public DeleteException(Throwable cause)
    {
        super(cause);
    }

    protected DeleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
