package com.kotan4ik.utbotmodule.service;

import com.kotan4ik.utbotmodule.Buttons;
import com.kotan4ik.utbotmodule.bot.Bot;
import com.kotan4ik.utbotmodule.dto.ReadingsDto;
import com.kotan4ik.utbotmodule.dto.TariffsDto;
import com.kotan4ik.utbotmodule.storage.readings.Readings;
import com.kotan4ik.utbotmodule.storage.readings.ReadingsRepository;
import com.kotan4ik.utbotmodule.storage.tariffs.Tariffs;
import com.kotan4ik.utbotmodule.storage.tariffs.TariffsRepository;
import com.kotan4ik.utbotmodule.storage.users.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.kotan4ik.utbotmodule.Buttons.*;
import static com.kotan4ik.utbotmodule.service.constants.MessageText.*;

@Component
@RequiredArgsConstructor
public class ServiceImpl implements Service {
    private final UsersRepository usersRepository;
    private final ReadingsRepository readingsRepository;
    private final TariffsRepository tariffsRepository;

    private Bot bot;

    @Autowired
    public void setBot(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void sendWelcomeMessage(long chatId) {
        Map<String, String> welcomeButtons = new HashMap<>();
        for (Buttons button : Buttons.values()) {
            if (button.isMenuButton())
                welcomeButtons.put(button.getButtonText(), button.getCommand());
        }
        bot.sendTextMessageWithButtons(chatId, GENERAL_WELCOME, welcomeButtons);
    }

    @Override
    public void welcomeNewReadings(long chatId) {
        Map<String, String> cancelButton = new HashMap<>();
        cancelButton.put(Buttons.CANCEL.getButtonText(), CANCEL.getCommand());
        bot.sendTextMessageWithButtons(chatId, SAVE_READINGS_WELCOME, cancelButton);
        usersRepository.saveUserChatMode(chatId, RECEIVE_NEW_READINGS);
    }

    @Override
    public void welcomeSaveTariffs(long chatId) {
        Map<String, String> cancelButton = new HashMap<>();
        cancelButton.put(Buttons.CANCEL.getButtonText(), CANCEL.getCommand());
        bot.sendTextMessageWithButtons(chatId, SAVE_TARIFFS_WELCOME, cancelButton);
        usersRepository.saveUserChatMode(chatId, SAVE_TARIFFS);
    }

    @Override
    public void processCalculation(long chatId) {
        bot.sendTextMessage(chatId, CALCULATION_WAITING);
        Optional<ReadingsDto> lastReadingsOptional = readingsRepository.getLastReading(chatId);
        Optional<ReadingsDto> previousReadingsOptional = readingsRepository.getPreviousReading(chatId);
        Optional<TariffsDto> lastTariffsOptional = tariffsRepository.getLastTariffs(chatId);
        if (lastReadingsOptional.isEmpty() || previousReadingsOptional.isEmpty()) {
            bot.sendTextMessage(chatId, READINGS_NOT_FOUND);
        } else if (lastTariffsOptional.isEmpty()) {
            bot.sendTextMessage(chatId, TARIFFS_NOT_FOUND);
        } else {
            double total = calculateSum(lastReadingsOptional.get(), previousReadingsOptional.get(), lastTariffsOptional.get());
            bot.sendTextMessage(chatId, CALCULATION_DONE + total);
            sendWelcomeMessage(chatId);
        }


    }

    @Override
    public void processNewReadings(long chatId, String readings) {
        List<String> readingsList = List.of(readings.split(";"));
        List<Integer> intReadings = new ArrayList<>();
        for (String reading : readingsList) {
            intReadings.add(Integer.parseInt(reading));
        }
        Readings reading = Readings.builder().userId(chatId).t1(intReadings.get(0)).t2(intReadings.get(1)).t3(intReadings.get(2)).hot(intReadings.get(3)).cold(intReadings.get(4)).build();
        readingsRepository.save(reading);
        usersRepository.resetMode(chatId);
        bot.sendTextMessage(chatId, READINGS_SAVED);
        sendWelcomeMessage(chatId);

    }

    @Override
    public void processNewTariffs(long chatId, String readings) {
        List<String> tariffsList = List.of(readings.split(";"));
        List<Integer> intTariffs = new ArrayList<>();
        for (String tariff : tariffsList) {
            Double doubleTariff = Double.parseDouble(tariff.replace(",", ".")) * 100;
            intTariffs.add((int) Math.round(doubleTariff));
        }
        Tariffs tariff = Tariffs.builder().userId(chatId).t1(intTariffs.get(0)).t2(intTariffs.get(1)).t3(intTariffs.get(2)).hot(intTariffs.get(3)).cold(intTariffs.get(4)).build();
        tariffsRepository.save(tariff);
        usersRepository.resetMode(chatId);
        bot.sendTextMessage(chatId, TARIFFS_SAVED);
        sendWelcomeMessage(chatId);

    }

    @Override
    public void renewDialog(long chatId) {
        usersRepository.resetMode(chatId);
        sendWelcomeMessage(chatId);
    }

    @Override
    public Optional<Buttons> getUserChatMode(long chatId) {
        return usersRepository.getUserChatMode(chatId);
    }

    private double calculateSum(ReadingsDto lastReadings, ReadingsDto previousReadings, TariffsDto tariffs) {
        int sum = 0;
        int[] lastReadingsArray = {lastReadings.getT1(), lastReadings.getT2(), lastReadings.getT3(), lastReadings.getCold(), lastReadings.getHot()};
        int[] previousReadingsArray = {previousReadings.getT1(), previousReadings.getT2(), previousReadings.getT3(), previousReadings.getCold(), previousReadings.getHot()};
        int[] tariffsArray = {tariffs.getT1(), tariffs.getT2(), tariffs.getT3(), tariffs.getCold(), tariffs.getHot()};

        for (int i = 0; i < lastReadingsArray.length; i++) {
            sum += (lastReadingsArray[i] - previousReadingsArray[i]) * tariffsArray[i];
        }

        double total = sum / 100;
        return total;
    }
}
