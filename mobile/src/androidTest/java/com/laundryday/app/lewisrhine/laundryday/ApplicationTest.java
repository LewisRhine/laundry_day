package com.laundryday.app.lewisrhine.laundryday;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

    cardUnitTest();


    }

    public void cardUnitTest(){
        Card card = new Card();

        card.setCardBalance(20.00);
        assertEquals(20.00, card.getCardBalance());

        card.addToCard(10d);
        assertEquals(30.00, card.getCardBalance());

        card.addToCard(2.50);
        assertEquals(32.50, card.getCardBalance());

        card.subtractFromCard(30d);
        assertEquals(2.50, card.getCardBalance());

        card.subtractFromCard(0.50);
        assertEquals(2d, card.getCardBalance());

    }


}