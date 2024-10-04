package org;

/**
 *
 * @author AidenFox
 */
public class SysUtils {
    private static String scriptName = "[FoxesSound]";
    public static void sendErr(String Error){
        String ErrorStyle = scriptName+"[Error] " + Error;
        System.err.println(ErrorStyle);
    }
    
    public static void send(String Message){
        String messageStyle = scriptName+"[Info]"+Message;
        System.out.println(messageStyle);
    }

    public static void sendException(RuntimeException ex) {
        String exceptionStyle = scriptName+"[Exception] "+ex;
        System.err.println(exceptionStyle);
    }
}
