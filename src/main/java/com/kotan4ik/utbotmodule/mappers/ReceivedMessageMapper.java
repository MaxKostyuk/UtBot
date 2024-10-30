package com.kotan4ik.utbotmodule.mappers;

import com.kotan4ik.utbotmodule.models.ReceivedMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ReceivedMessageMapper {
    public static ReceivedMessage toMessage(Update updateEvent) {
        long id;

        if (updateEvent.hasMessage()) {
            id = updateEvent.getMessage().getFrom().getId();
        }else if (updateEvent.hasCallbackQuery()) {
            id = updateEvent.getCallbackQuery().getFrom().getId();
        } else {
            throw new IllegalArgumentException("Unsupported type of message");
        }

        String command = updateEvent.hasCallbackQuery() ? updateEvent.getCallbackQuery().getData() : null;

        String text = updateEvent.hasMessage() ? updateEvent.getMessage().getText() : null;

        return new ReceivedMessage(id, command, text);
    }
}
