package core.disk;

public class NoFreeDiskSpaceException extends Exception
{
    public NoFreeDiskSpaceException()
    {
        super();
    }

    public NoFreeDiskSpaceException(String message)
    {
        super(message);
    }

    public NoFreeDiskSpaceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public NoFreeDiskSpaceException(Throwable cause)
    {
        super(cause);
    }

    protected NoFreeDiskSpaceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
