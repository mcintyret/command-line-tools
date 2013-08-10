package com.mcintyret.commandline;

/**
 * User: mcintyret2
 * Date: 10/08/2013
 */

import java.util.*;

import static com.mcintyret.commandline.Utils.addIfAbsent;
import static com.mcintyret.commandline.Utils.putIfAbsent;

public class CommandLineInput {

    private final Set<CommandLineOption> flags = new HashSet<>();

    private final Map<CommandLineOption, String> optionValues = new HashMap<>();

    private final Deque<String> args = new ArrayDeque<>();

    private final CommandLineOptions options;

    public CommandLineInput(CommandLineOptions options) {
        this.options = options;
    }

    public static CommandLineInput parseInput(String[] args, CommandLineOptions options, boolean ignoreInvalid) {
        CommandLineInput input = new CommandLineInput(options);

        boolean programArgsStarted = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("-")) {
                // this is option(s) or flag(s)
                if (programArgsStarted) {
                    throw new IllegalArgumentException("All flags and options must appear before program arguments");
                }

                if (arg.startsWith("--")) {
                    CommandLineOption opt = getOption(arg.substring(2), ignoreInvalid, options);
                    if (opt != null) {
                        switch (opt.getArg()) {
                            case YES:
                                i = addRequiredArg(args, input, i, opt);
                                break;
                            case NO:
                                input.addFlag(opt);
                                break;
                            case OPTIONAL:
                                i = addOptionalArg(args, input, i, opt);
                                break;
                        }
                    }
                } else {
                    // this is an option/flag with a single dash, which means there may be multiple, and each
                    // one consists of a single character.
                    // At most one of these options may take an argument.
                    List<CommandLineOption> opts = new ArrayList<>();
                    for (int j = 1; j < arg.length(); j++) {
                        opts.add(getOption(arg.charAt(j), ignoreInvalid, options));
                    }
                    CommandLineOption withArg = null;
                    CommandLineOption.Arg argType = null;
                    for (CommandLineOption opt : opts) {
                        if (opt != null) {
                            switch (opt.getArg()) {
                                case YES:
                                    if (withArg != null && argType == CommandLineOption.Arg.YES) {
                                        throw new IllegalArgumentException("When multiple options are provided in a group, only one may require an argument: '" + arg + "'");
                                    }
                                    withArg = opt;
                                    argType = CommandLineOption.Arg.YES;
                                    break;
                                case NO:
                                    input.addFlag(opt);
                                    break;
                                case OPTIONAL:
                                    if (withArg != null && argType == CommandLineOption.Arg.OPTIONAL) {
                                        throw new IllegalArgumentException("Ambiguous input: multiple options provided which take an optional argument: '" + arg + "'");
                                    }
                                    withArg = opt;
                                    argType = CommandLineOption.Arg.OPTIONAL;
                                    break;
                            }
                        }
                    }
                    if (argType != null) {
                        switch (argType) {
                            case YES:
                                i = addRequiredArg(args, input, i, withArg);
                                break;
                            case OPTIONAL:
                                i = addOptionalArg(args, input, i, withArg);
                                break;
                            default:
                                throw new AssertionError("Can't happen");
                        }
                    }
                }
            } else {
                programArgsStarted = true;
                input.args.add(arg);
            }
        }
        return input;
    }

    private static int addRequiredArg(String[] args, CommandLineInput input, int i, CommandLineOption withArg) {
        if (i + 1 >= args.length) {
            throw new IllegalArgumentException("Option " + withArg + " requires an argument");
        }
        String requiredArg = args[++i];
        if (requiredArg.startsWith("-")) {
            throw new IllegalArgumentException("Option " + withArg + " requires an argument");
        }
        input.addOption(withArg, requiredArg);
        return i;
    }

    private static int addOptionalArg(String[] args, CommandLineInput input, int i, CommandLineOption withArg) {
        if (i + 1 >= args.length) {
            // no arg present, but that's OK
            input.addFlag(withArg);
        } else {
            String possibleArg = args[i + 1];
            if (possibleArg.startsWith("-")) {
                // this is another option, not an arg value
                input.addFlag(withArg);
            } else {
                i++;
                input.addOption(withArg, possibleArg);
            }
        }
        return i;
    }

    private static CommandLineOption getOption(char c, boolean ignoreInvalid, CommandLineOptions options) {
        CommandLineOption option = options.getOption(c);
        if (option == null && !ignoreInvalid) {
            throw new IllegalArgumentException("Invalid option: " + c);
        }
        return option;
    }

    private static CommandLineOption getOption(String str, boolean ignoreInvalid, CommandLineOptions options) {
        CommandLineOption option = options.getOption(str);
        if (option == null && !ignoreInvalid) {
            throw new IllegalArgumentException("Invalid option: " + str);
        }
        return option;
    }

    private void addFlag(CommandLineOption flag) {
        addIfAbsent(flags, flag, "Option specified by " + flag + " has been set multiple times");
    }

    private void addOption(CommandLineOption option, String value) {
        putIfAbsent(optionValues, option, value, "Option specified by " + option + " has been set multiple times");
    }


    public boolean hasFlag(String flag) {
        return flags.contains(options.getOption(flag));
    }

    public boolean hasFlag(char flag) {
        return flags.contains(options.getOption(flag));
    }

    public String nextArg() {
        return args.pollFirst();
    }

    public String peekArg() {
        return args.peekFirst();
    }

    public List<String> allArgs() {
        return new ArrayList<>(args);
    }

    public int remainingArgs() {
        return args.size();
    }

    @Override
    public String toString() {
        return "Flags: " + flags + ", Options: " + optionValues + ", Args: " + args;
    }
}
