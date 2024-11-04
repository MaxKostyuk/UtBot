package com.kotan4ik.utbotmodule.models;

public record ReceivedMessage(long chatId, String command, String messageText) {
}
