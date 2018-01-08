package mundosk_libraries.java_websocket.exceptions;

import mundosk_libraries.java_websocket.framing.CloseFrame;

/**
 * exception which indicates that a invalid frame was recieved (CloseFrame.PROTOCOL_ERROR)
 */
public class InvalidFrameException extends InvalidDataException {

    /**
     * Serializable
     */
    private static final long serialVersionUID = -9016496369828887591L;

    /**
     * constructor for a InvalidFrameException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     */
    public InvalidFrameException() {
        super(CloseFrame.PROTOCOL_ERROR);
    }

    /**
     * constructor for a InvalidFrameException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     *
     * @param s the detail message.
     */
    public InvalidFrameException(String s) {
        super(CloseFrame.PROTOCOL_ERROR, s);
    }

    /**
     * constructor for a InvalidFrameException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     *
     * @param t the throwable causing this exception.
     */
    public InvalidFrameException(Throwable t) {
        super(CloseFrame.PROTOCOL_ERROR, t);
    }

    /**
     * constructor for a InvalidFrameException
     * <p>
     * calling InvalidDataException with closecode PROTOCOL_ERROR
     *
     * @param s the detail message.
     * @param t the throwable causing this exception.
     */
    public InvalidFrameException(String s, Throwable t) {
        super(CloseFrame.PROTOCOL_ERROR, s, t);
    }
}
