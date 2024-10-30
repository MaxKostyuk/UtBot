package com.kotan4ik.utbotmodule.bot;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

public interface Bot {

    Message sendTextMessage(long chatId, String text);
    Message sendTextMessageWithButtons(long chatId, String text, Map<String, String> buttonMap);
}
