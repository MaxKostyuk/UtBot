package com.kotan4ik.utbotmodule.controller;

import com.kotan4ik.utbotmodule.models.ReceivedMessage;

public interface Controller {

    void onUpdateReceived(ReceivedMessage message);
}
