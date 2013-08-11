package com.mcintyret.commandline;

import java.util.HashMap;
import java.util.Map;

import static com.mcintyret.commandline.Utils.putIfAbsent;

/**
 * User: mcintyret2
 * Date: 10/08/2013
 */
public class CommandLineOptions {

    private final Map<String, CommandLineOption> stringOptions = new HashMap<>();

    private final Map<Character, CommandLineOption> charOptions = new HashMap<>();

    static final CommandLineOptions NO_OPTIONS = new CommandLineOptions();

    public CommandLineOptions(CommandLineOption... options) {
        for (CommandLineOption option : options) {
            putIfAbsent(charOptions, option.getC(), option);
            for (String string : option.getFullArgs()) {
                putIfAbsent(stringOptions, string, option);
            }
        }
    }

    public CommandLineOption getOption(char c) {
        return charOptions.get(c);
    }

    public CommandLineOption getOption(String str) {
        return stringOptions.get(str);
    }


}
