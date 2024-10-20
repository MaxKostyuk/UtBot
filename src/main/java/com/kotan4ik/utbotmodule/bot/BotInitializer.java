package com.kotan4ik.utbotmodule.bot;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

    private final UtilityBot utilityBot;

    public BotInitializer(UtilityBot utilityBot) {
        this.utilityBot = utilityBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(utilityBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
