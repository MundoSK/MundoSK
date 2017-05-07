package mundosk_libraries.java_websocket.exceptions;

import mundosk_libraries.java_websocket.framing.CloseFrame;

/**
 * exception which indicates that the message limited was exedeeded (CloseFrame.TOOBIG)
 */
public class LimitExedeedException extends InvalidDataException {

    /**
     * Serializable
     */
    private static final long serialVersionUID = 6908339749836826785L;

    /**
     * constructor for a LimitExedeedException
     * <p>
     * calling InvalidDataException with closecode TOOBIG
     */
    public LimitExedeedException() {
        super(CloseFrame.TOOBIG);
    }

    /**
     * constructor for a LimitExedeedException
     * <p>
     * calling InvalidDataException with closecode TOOBIG
     *
     * @param s the detail message.
     */
    public LimitExedeedException(String s) {
        super(CloseFrame.TOOBIG, s);
    }

}
