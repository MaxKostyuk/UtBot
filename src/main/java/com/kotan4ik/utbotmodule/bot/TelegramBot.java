package com.kotan4ik.utbotmodule.bot;

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

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    private ThreadLocal<Update> updateEvent = new ThreadLocal<>();

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
        try {
            this.updateEvent.set(updateEvent);
            onUpdateEventReceived(this.updateEvent.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onUpdateEventReceived(Update update) {
        //do nothing
    }

    public Long getCurrentChatId() {
        if (updateEvent.get().hasMessage()) {
            return updateEvent.get().getMessage().getFrom().getId();
        }
        if (updateEvent.get().hasCallbackQuery()) {
            return updateEvent.get().getCallbackQuery().getFrom().getId();
        }
        return null;
    }

    public String getMessageText() {
        return updateEvent.get().hasMessage() ? updateEvent.get().getMessage().getText() : "";
    }

    public Message sendTextMessage(String text)  {
        SendMessage command = createApiSendMessageCommand(text);
        return executeTelegramApiMethod(command);
    }

    public Message sendTextMessageWithButtons(String text, Map<String, String> buttonMap) {
        SendMessage message = createApiSendMessageCommand(text);
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

    private SendMessage createApiSendMessageCommand(String text) {
        SendMessage message = new SendMessage();
        message.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        message.setParseMode("markdown");
        message.setChatId(getCurrentChatId());
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
