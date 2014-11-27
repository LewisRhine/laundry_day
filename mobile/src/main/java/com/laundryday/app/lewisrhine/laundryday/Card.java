package com.laundryday.app.lewisrhine.laundryday;

public class Card {

    private Double cardBalance = 0d;

    public void addToCard(Double howMuch) {
        if (howMuch > 0) {
            cardBalance += howMuch;
        }
    }

    public void subtractFromCard(Double howMuch) {
        if (howMuch > 0) {
            cardBalance -= howMuch;
        }
    }

    public Double getCardBalance() {
        return cardBalance;
    }

    public void setCardBalance(Double cardBalance) {
        this.cardBalance = cardBalance;
    }
}
