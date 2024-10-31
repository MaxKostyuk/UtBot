package com.kotan4ik.utbotmodule.controller;

import com.kotan4ik.utbotmodule.Buttons;
import com.kotan4ik.utbotmodule.models.ReceivedMessage;
import com.kotan4ik.utbotmodule.service.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BotController implements Controller {

    private final Service service;

    public BotController(@Lazy Service service) {
        /** Аннотация @Lazy необходима для разрыва цикличной зависимости бинов **/
        this.service = service;
    }

    @Override
    public void onUpdateReceived(ReceivedMessage message) {
        long chatId = message.chatId();
        if (message.command() != null) {
            Buttons receivedCommand = Buttons.getByCommand(message.command());
            switch (receivedCommand) {
                case RECEIVE_NEW_READINGS -> service.welcomeNewReadings(chatId);
                case CALCULATE_SUM -> service.processCalculation(chatId);
                case SAVE_TARIFFS -> service.welcomeSaveTariffs(chatId);
                case CANCEL -> service.renewDialog(chatId);
            }
        } else {
            Optional<Buttons> mode = service.getUserChatMode(chatId);
            if (mode.isPresent()) {
                switch (mode.get()) {
                    case RECEIVE_NEW_READINGS -> service.processNewReadings(chatId, message.messageText());
                    case SAVE_TARIFFS -> service.processNewTariffs(chatId, message.messageText());
                }

            } else {
                service.sendWelcomeMessage(message.chatId());
            }
        }
    }
}
