package mundosk_libraries.java_websocket.exceptions;

import mundosk_libraries.java_websocket.framing.CloseFrame;

/**
 * exception which indicates that a invalid request was recieved (CloseFrame.PROTOCOL_ERROR)
 */
public class InvalidHandshakeException extends InvalidDataException {

    /**
     * Serializable
     */
    private static final long serialVersionUID = -1426533877490484964L;

    /**
     * constructor for a InvalidHandshakeException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     */
    public InvalidHandshakeException() {
        super(CloseFrame.PROTOCOL_ERROR);
    }

    /**
     * constructor for a InvalidHandshakeException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     *
     * @param s the detail message.
     * @param t the throwable causing this exception.
     */
    public InvalidHandshakeException(String s, Throwable t) {
        super(CloseFrame.PROTOCOL_ERROR, s, t);
    }

    /**
     * constructor for a InvalidHandshakeException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     *
     * @param s the detail message.
     */
    public InvalidHandshakeException(String s) {
        super(CloseFrame.PROTOCOL_ERROR, s);
    }

    /**
     * constructor for a InvalidHandshakeException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     *
     * @param t the throwable causing this exception.
     */
    public InvalidHandshakeException(Throwable t) {
        super(CloseFrame.PROTOCOL_ERROR, t);
    }

}
