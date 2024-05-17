package exceptions;

import java.io.Serial;

public class CloningException extends RuntimeException
{
    @Serial
    private static final long	serialVersionUID	= 3815175312001146867L;

    public CloningException(final String message, final Throwable cause)
    {
        super(message, cause);

    }

    public CloningException(String message) {
        super(message);
    }

    public CloningException(Exception e) {
        super(e);
    }
}