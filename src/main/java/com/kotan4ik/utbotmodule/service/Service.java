package com.kotan4ik.utbotmodule.service;

import com.kotan4ik.utbotmodule.Buttons;

import java.util.Optional;

public interface Service {

    void sendWelcomeMessage(long chatId);
    void welcomeNewReadings(long chatId);
    void welcomeSaveTariffs(long chatId);
    void processCalculation(long chatId);
    void processNewReadings(long chatId, String readings);
    void processNewTariffs(long chatId, String readings);
    void renewDialog(long chatId);
    Optional<Buttons> getUserChatMode(long chatId);
}
