package com.kotan4ik.utbotmodule.bot;

public record ReceivedMessage(long chatId, String command, String messageText) {
}
