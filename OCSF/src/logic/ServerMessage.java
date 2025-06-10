
package logic;

import java.io.Serializable;

/**
 * Represents a message sent from the server to the client.
 * Implements Serializable to allow the message to be sent over a network.
 */
public class ServerMessage implements Serializable {
    private Object msg; // The message content
    private String command; // The command associated with the message

    /**
     * Constructs a new ServerMessage with the specified message content and command.
     *
     * @param msg the message content
     * @param command the command associated with the message
     */
    public ServerMessage(Object msg, String command) {
        this.msg = msg;
        this.command = command;
    }

    /**
     * Gets the message content.
     *
     * @return the message content
     */
    public Object getMsg() {
        return msg;
    }

    /**
     * Sets the message content.
     *
     * @param msg the message content
     */
    public void setMsg(Object msg) {
        this.msg = msg;
    }

    /**
     * Gets the command associated with the message.
     *
     * @return the command associated with the message
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the command associated with the message.
     *
     * @param command the command associated with the message
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Returns a string representation of the ServerMessage.
     *
     * @return a string representation of the ServerMessage
     */
    @Override
    public String toString() {
        return "ServerMessage{command='" + command + "', msg=" + msg + "}";
    }
}
