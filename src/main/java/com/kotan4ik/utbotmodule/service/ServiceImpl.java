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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Map<String, String> welcomeButtons = Buttons.stream()
                .filter(Buttons::isMenuButton)
                .collect(Collectors.toMap(Buttons::getButtonText, Buttons::getCommand));
        bot.sendTextMessageWithButtons(chatId, GENERAL_WELCOME, welcomeButtons);
    }

    @Override
    public void welcomeNewReadings(long chatId) {
        sendMessageWithCancelButton(chatId, SAVE_READINGS_WELCOME);
        usersRepository.saveUserChatMode(chatId, RECEIVE_NEW_READINGS);
    }

    @Override
    public void welcomeSaveTariffs(long chatId) {
        sendMessageWithCancelButton(chatId, SAVE_TARIFFS_WELCOME);
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
    public void processNewReadings(long chatId, String stringReadings) {
        try {
            List<Integer> intReadings = parseStringReadingData(stringReadings, ";");
            Readings readings = integersToReadings(chatId, intReadings);
            readingsRepository.save(readings);
            finalizeProcess(chatId, READINGS_SAVED);
        } catch (Exception e) {
            sendMessageWithCancelButton(chatId, DATA_PARSE_ERROR);
        }
    }

    @Override
    public void processNewTariffs(long chatId, String stringTariffs) {
        try {
            List<Integer> intTariffs = parseStringTariffsData(stringTariffs, ";");
            Tariffs tariff = integersToTariffs(chatId, intTariffs);
            tariffsRepository.save(tariff);
            finalizeProcess(chatId, TARIFFS_SAVED);
        }catch (Exception e) {
            sendMessageWithCancelButton(chatId, DATA_PARSE_ERROR);
        }
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

        sum += (lastReadings.getT1() - previousReadings.getT1()) * tariffs.getT1();
        sum += (lastReadings.getT2() - previousReadings.getT2()) * tariffs.getT2();
        sum += (lastReadings.getT3() - previousReadings.getT3()) * tariffs.getT3();
        sum += (lastReadings.getCold() - previousReadings.getCold()) * tariffs.getCold();
        sum += (lastReadings.getHot() - previousReadings.getHot()) * tariffs.getHot();

        return sum / 100.0;
    }

    private List<Integer> parseStringReadingData(String data, String delimiter) {
        return Stream.of(data.split(delimiter))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

    }

    private Readings integersToReadings(long userId, List<Integer> intReadings) {
        return Readings.builder()
                .userId(userId)
                .t1(intReadings.get(0))
                .t2(intReadings.get(1))
                .t3(intReadings.get(2))
                .hot(intReadings.get(3))
                .cold(intReadings.get(4))
                .build();
    }

    private List<Integer> parseStringTariffsData(String stringTariffs, String delimiter) {
        return Stream.of(stringTariffs.split(delimiter))
                .map(tariffString -> {
                    String tariff = tariffString.replace(",", ".").trim();
                    double tariffDouble = Double.parseDouble(tariff);
                    return (int) Math.round(tariffDouble * 100);
                })
                .collect(Collectors.toList());
    }

    private Tariffs integersToTariffs(long userId, List<Integer> intTariffs) {
        return Tariffs.builder()
                .userId(userId)
                .t1(intTariffs.get(0))
                .t2(intTariffs.get(1))
                .t3(intTariffs.get(2))
                .hot(intTariffs.get(3))
                .cold(intTariffs.get(4))
                .build();
    }

    private void sendMessageWithCancelButton(long chatId, String message) {
        Map<String, String> cancelButton = new HashMap<>();
        cancelButton.put(Buttons.CANCEL.getButtonText(), CANCEL.getCommand());
        bot.sendTextMessageWithButtons(chatId, message, cancelButton);
    }

    private void finalizeProcess(long chatId, String message) {
        usersRepository.resetMode(chatId);
        bot.sendTextMessage(chatId, message);
        sendWelcomeMessage(chatId);
    }
}
