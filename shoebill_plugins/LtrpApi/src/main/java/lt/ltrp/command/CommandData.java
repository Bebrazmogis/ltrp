package lt.ltrp.command;

/**
 * @author Bebras
 *         2015.11.13.
 */
public class CommandData {

    private Class<?> origin;
    private Class<?>[] paramTypes;
    private String[] paramNames;
    private String name;
    private CommandHandler handler;
    private boolean isCaseSensitive;
    private String helpMessage;

    public CommandData() {

    }

    public CommandData(Class<?> origin, Class<?>[] paramTypes, String[] paramNames, String name, CommandHandler handler, boolean isCaseSensitive, String helpMessage) {
        this.origin = origin;
        this.paramTypes = paramTypes;
        this.paramNames = paramNames;
        this.name = name;
        this.handler = handler;
        this.isCaseSensitive = isCaseSensitive;
        this.helpMessage = helpMessage;
    }

    public CommandData(Class<?>[] paramTypes, String[] paramNames, String name, CommandHandler handler, boolean isCaseSensitive, String helpMessage) {
        this.paramTypes = paramTypes;
        this.paramNames = paramNames;
        this.name = name;
        this.handler = handler;
        this.isCaseSensitive = isCaseSensitive;
        this.helpMessage = helpMessage;
    }


    public Class<?> getOrigin() {
        return origin;
    }

    public void setOrigin(Class<?> origin) {
        this.origin = origin;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    public void setCaseSensitive(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public void setParamNames(String[] paramNames) {
        this.paramNames = paramNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommandHandler getHandler() {
        return handler;
    }

    public void setHandler(CommandHandler handler) {
        this.handler = handler;
    }
}
