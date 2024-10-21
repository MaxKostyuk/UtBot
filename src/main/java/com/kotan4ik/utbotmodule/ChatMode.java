package com.kotan4ik.utbotmodule;

public enum ChatMode {
    RECEIVE_NEW_READINGS("newReadings", "Передать новые показания в бот"),
    CALCULATE_SUM("calculateSum", "Расчет оплаты"),
    SEND_READINGS("sendReadings", "Передать показания в УК");
    private final String command;
    private final String buttonText;

    ChatMode(String command, String buttonText) {
        this.command = command;
        this.buttonText = buttonText;
    }

    public String getCommand() {
        return command;
    }

    public String getButtonText() {
        return buttonText;
    }

    public static ChatMode getByCommand(String command) {
        for(ChatMode mode : ChatMode.values()) {
            if (mode.getCommand().equals(command))
                return mode;
        }
        throw new IllegalArgumentException("No mode found for this command");
    }
}
