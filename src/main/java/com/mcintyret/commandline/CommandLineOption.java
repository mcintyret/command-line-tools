package com.mcintyret.commandline;

import static com.mcintyret.commandline.Utils.checkArgument;

/**
 * User: mcintyret2
 * Date: 10/08/2013
 */
public class CommandLineOption {

    public enum Arg {
        YES,
        NO,
        OPTIONAL   // Avoid if possible, it complicates processing and adds ambiguity
    }

    private final Arg arg;

    private final char c;

    private final String[] fullArgs;

    public CommandLineOption(Arg arg, char c, String... fullArgs) {
        checkArgument(Character.isLetterOrDigit(c));
        this.arg = arg;
        this.c = c;
        this.fullArgs = fullArgs;
    }

    Arg getArg() {
        return arg;
    }

    char getC() {
        return c;
    }

    String[] getFullArgs() {
        return fullArgs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append("-").append(c);
        for (String fullArg : fullArgs) {
            sb.append(" --").append(fullArg);
        }
        return sb.append("]").toString();
    }
}
