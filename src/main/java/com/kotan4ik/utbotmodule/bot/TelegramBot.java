package com.kotan4ik.utbotmodule.bot;

import com.kotan4ik.utbotmodule.controller.Controller;
import com.kotan4ik.utbotmodule.mappers.ReceivedMessageMapper;
import com.kotan4ik.utbotmodule.models.ReceivedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot implements Bot {

    @Value("${bot.name")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    private final Controller controller;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public final void onUpdateReceived(Update updateEvent) {
        ReceivedMessage receivedMessage = ReceivedMessageMapper.toMessage(updateEvent);
        controller.onUpdateReceived(receivedMessage);
    }

    public Message sendTextMessage(long chatId, String text) {
        SendMessage command = createApiSendMessageCommand(chatId, text);
        return executeTelegramApiMethod(command);
    }

    public Message sendTextMessageWithButtons(long chatId, String text, Map<String, String> buttonMap) {
        SendMessage message = createApiSendMessageCommand(chatId, text);
        attachButtons(message, buttonMap);
        return executeTelegramApiMethod(message);
    }

    private void attachButtons(SendMessage message, Map<String, String> buttonMap) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Map.Entry<String, String> buttonDescription : buttonMap.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(new String(buttonDescription.getKey().getBytes(), StandardCharsets.UTF_8));
            button.setCallbackData(buttonDescription.getValue());
            keyboard.add(List.of(button));
        }

        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);
    }

    private SendMessage createApiSendMessageCommand(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        message.setParseMode("markdown");
        message.setChatId(chatId);
        return message;
    }

    private <T extends Serializable, Method extends BotApiMethod<T>> T executeTelegramApiMethod(Method method) {
        try {
            return super.sendApiMethod(method);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
