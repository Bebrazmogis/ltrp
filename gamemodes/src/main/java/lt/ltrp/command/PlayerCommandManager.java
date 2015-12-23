package lt.ltrp.command;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CustomCommandHandler;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.shoebill.common.command.PlayerCommandManager.UsageMessageSupplier;
import net.gtaun.util.event.HandlerPriority;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Bebras
 *         2015.11.13.
 */
public class PlayerCommandManager {

    private static final Map<Class<?>, Function<String, Object>> TYPE_PARSER = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(PlayerCommandManager.class);


    private HandlerPriority priority;
    private EventManager eventManager;
    private UsageMessageSupplier usageMessageSupplier;
    private Map<String, String> commandUsageMessages = new HashMap<>();


    private Map<String, Collection<CommandData>> commands;
    private Map<Class<?>, CustomCommandHandler> beforeCheckers;

    private static final UsageMessageSupplier DEFAULT_USAGE_MESSAGE_SUPPLIER = (p, cmd, prefix, params, help) -> {
        String message = "Naudojimas: " + prefix + cmd;
        for (String param : params)
            message += " [" + param + "]";
        return message;
    };

    static {
        // Coppied from Sheobill
        TYPE_PARSER.put(String.class, (s) -> s);

        TYPE_PARSER.put(int.class, Integer::parseInt);
        TYPE_PARSER.put(Integer.class, (s) -> Integer.parseInt(s));

        TYPE_PARSER.put(short.class, (s) -> Short.parseShort(s));
        TYPE_PARSER.put(Short.class, (s) -> Short.parseShort(s));

        TYPE_PARSER.put(byte.class, (s) -> Byte.parseByte(s));
        TYPE_PARSER.put(Byte.class, (s) -> Byte.parseByte(s));

        TYPE_PARSER.put(char.class, (s) -> s.length() > 0 ? s.charAt(0) : 0);
        TYPE_PARSER.put(Character.class, (s) -> s.length() > 0 ? s.charAt(0) : 0);

        TYPE_PARSER.put(float.class, (s) -> Float.parseFloat(s));
        TYPE_PARSER.put(Float.class, (s) -> Float.parseFloat(s));

        TYPE_PARSER.put(double.class, (s) -> Double.parseDouble(s));
        TYPE_PARSER.put(Double.class, (s) -> Double.parseDouble(s));

        TYPE_PARSER.put(Boolean.class, (s) -> Boolean.parseBoolean(s));

        TYPE_PARSER.put(Player.class, (s) -> Player.getByNameOrId(s));
        TYPE_PARSER.put(Color.class, (s) -> new Color(Integer.parseUnsignedInt(s, 16)));

        // Custom defaults
        TYPE_PARSER.put(LtrpPlayer.class, (s) -> LtrpPlayer.get(Player.getByNameOrId(s)));
        TYPE_PARSER.put(Vehicle.class, (s) -> Vehicle.get(Integer.parseInt(s)));

    }

    private static Object parseParam(Class<?> type, String param) {
        logger.debug("PlayerCommandManager :: parseParam. PAram type:" + type.getName() + " param value:" + param);
        if(TYPE_PARSER.containsKey(type)) {
            logger.debug("PARSER for this parameter exists. After parse:" + TYPE_PARSER.get(type).apply(param));
            return TYPE_PARSER.get(type).apply(param);
        }
        else return null;
    }

    private static Object[] parseParams(Class<?>[] types, String[] paramVals) {
        Object[] params = new Object[types.length];
        for (int i = 0; i < types.length; i++)
            params[i] = parseParam(types[i], paramVals[i]);
        return params;
    }

    public PlayerCommandManager(HandlerPriority priority, EventManager manager) {
        this.priority = priority;
        this.eventManager = manager;
        commands = new HashMap<>();
        beforeCheckers = new HashMap<>();

        eventManager.registerHandler(PlayerCommandEvent.class, priority, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            logger.debug("Command " + e.getCommand() + " received");
            if(processCommand(player, e.getCommand().substring(1)))
                e.setProcessed();
        });
    }


    public boolean processCommand(LtrpPlayer player, String cmdText) {
        logger.debug("Processing command " + cmdText);
        int index;
        String cmdName = (index = cmdText.indexOf(" ")) == -1 ? cmdText : cmdText.substring(0, index);
        if(index != -1)
            cmdText = cmdText.substring(index+1);
        else
            cmdText = "";

        List<String> matches = new ArrayList<>();
        Matcher matcher = Pattern.compile("(\\S+?)\\S*").matcher(cmdText);
        while(matcher.find())
            matches.add(matcher.group());

        String[] data = matches.toArray(new String[0]);


        // If the command isn't registered here
        if(!commands.containsKey(cmdName)) {
            logger.debug("command not registered " + cmdName);
            return false;
        }

        for(CommandData commandData : commands.get(cmdName)) {
            if(beforeCheckers.containsKey(commandData.getOrigin())) {
                if(!beforeCheckers.get(commandData.getOrigin()).handle(player, cmdName, cmdText)) {
                    logger.debug("command checker for " + cmdName + " failed.");
                    return false;
                }
            }
            if(data.length != commandData.getParamTypes().length) {
                if(commandUsageMessages.containsKey(cmdName)) {
                    player.sendMessage(Color.NEWS, commandUsageMessages.get(cmdName));
                } else {
                    player.sendMessage(Color.NEWS, usageMessageSupplier != null ?
                            usageMessageSupplier.get(player, cmdName, "/", commandData.getParamNames(), commandData.getHelpMessage()) :
                            DEFAULT_USAGE_MESSAGE_SUPPLIER.get(player, cmdName, "/", commandData.getParamNames(), commandData.getHelpMessage()));
                }
                return true;
            } else {
                Object[] params = parseParams(commandData.getParamTypes(), data);
                params = ArrayUtils.add(params, 0, player);

                logger.info("Parsing params for command " + cmdName + "Param count:" + params.length + " param type count:" + commandData.getParamTypes().length);
                logger.info("Calling handler. Command name:" + commandData.getName());
                if(commandData.getHandler().handle(player, params))
                    return true;
            }

        }
        return false;
    }

    public void registerCommand(String command, Class<?>[] paramTypes, String[] paramNames, CommandHandler handler) {
        registerCommand(command, paramTypes, paramNames, handler, false, null);
    }

    public void registerCommand(String command, Class<?>[] paramTypes, String[] paramNames, CommandHandler handler, boolean caseSensitivity, String helpMsg) {
        registerCommand(new CommandData(paramTypes, paramNames, command, handler, caseSensitivity, helpMsg));

    }

    public void registerCommand(CommandData commandData) {
        logger.info("Registered command " + commandData.getName());
        Collection<CommandData> data = commands.get(commandData.getName());
        if(data == null) {
            data = new ArrayList<>();
            commands.put(commandData.getName(), data);
        }
        data.add(commandData);
    }

    public void setUsageMessageSupplier(UsageMessageSupplier usageMessageSupplier) {
        this.usageMessageSupplier = usageMessageSupplier;
    }

    public void setUsageMessage(String command, String message) {
        commandUsageMessages.put(command, message);
    }


    public void registerCommands(Object... objects) {
        for(Object object : objects) {

            Method[] methods = object.getClass().getMethods();
            for(Method m : methods) {
                CommandData data = new CommandData();
                Command commandAnnotation = m.getAnnotation(Command.class);
                BeforeCheck beforeCheckAnnotation = m.getAnnotation(BeforeCheck.class);
                if(commandAnnotation != null) {
                    data.setName(commandAnnotation.name().isEmpty() ? m.getName() : commandAnnotation.name());
                    data.setCaseSensitive(commandAnnotation.caseSensitive());
                    CommandHelp commandHelpAnnotation = m.getAnnotation(CommandHelp.class);
                    if(commandHelpAnnotation != null)
                        data.setHelpMessage(commandHelpAnnotation.value());

                    Parameter[] params = m.getParameters();

                    if(params[0].getType() != Player.class && params[0].getType() != LtrpPlayer.class)
                        return;
                    Class<?>[] types = new Class<?>[m.getParameterCount()-1];
                    String[] names = new String[m.getParameterCount()-1];

                    for(int i = 1; i < params.length; i++) {
                        CommandParam paramAnnotation = params[i].getAnnotation(CommandParam.class);
                        names[i-1] = paramAnnotation == null ? params[i].getName() : paramAnnotation.value();
                        types[i-1] = params[i].getType();
                        logger.debug("Parsing methods " + m.getName() + " params. Param "  + i + " is type:" + params[i].getType().getName() + " its name:" + params[i].getName());
                    }
                    data.setHandler((player, parameters) -> {
                        try {
                            logger.debug("param count:" + parameters.length);
                            return (boolean)m.invoke(object, parameters);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return false;
                    });
                    data.setParamTypes(types);
                    data.setParamNames(names);
                    data.setOrigin(object.getClass());
                    registerCommand(data);
                }
                if (beforeCheckAnnotation != null) {
                    beforeCheckers.put(object.getClass(), (p, cmd, params) -> {
                        try {
                            return (boolean) m.invoke(object, p, cmd, params);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return false;
                    });
                }
            }
        }
    }

    public void replaceTypeParser(Class<?> type, Function<String, Object> parser) {
        if(TYPE_PARSER.containsKey(type)) {
            TYPE_PARSER.remove(type);
        }
        TYPE_PARSER.put(type, parser);
    }

}
