/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package charlie.bot.server;

import charlie.advisor.BasicStrategy;
import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Dealer;
import charlie.dealer.Seat;
import charlie.plugin.IBot;
import charlie.util.Play;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @authors <MA-JM-DB>.
 */
public class MyBot implements Runnable, IBot {

    private Dealer dealer;
    private Hand hand;
    private Hid hid;
    private List<Hid> hList;
    private int shoeSize;
    private Card upCard;

    @Override
    public Hand getHand() {
        return this.hand;
    }

    @Override
    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void sit(Seat seat) {
        //Make an empty hand before sit.
        hand = new Hand(new Hid(seat));
        hid = new Hid(hand.getHid());
    }

    @Override
    public void startGame(List<Hid> hids, int shoeSize) {
        this.hList = new ArrayList<Hid>(hids.size());
        this.shoeSize = shoeSize;
    }

    @Override
    public void endGame(int shoeSize) {
        shoeSize = shoeSize;
    }

    @Override
    public void deal(Hid hid, Card card, int[] values) {
        if (Seat.DEALER == hid.getSeat()) {
            if (card != null) {
                upCard = new Card(card);
            }
        }
    }

    @Override
    public void insure() {
    }

    @Override
    public void bust(Hid hid) {
    }

    @Override
    public void win(Hid hid) {
    }

    @Override
    public void blackjack(Hid hid) {
    }

    @Override
    public void charlie(Hid hid) {
    }

    @Override
    public void lose(Hid hid) {
    }

    @Override
    public void push(Hid hid) {
    }

    @Override
    public void shuffling() {
    }

    @Override
    public void play(Hid hid) {

        if (this.hid.getSeat() == hid.getSeat()) {
            Thread t = new Thread(this);
            t.start();
        }
    }

    @Override
    public void run() {

        synchronized (dealer) {

            Responder r = new Responder(this.hand, this.upCard, this.dealer);

            Play bs = r.get_advice();

            while (bs != Play.STAY) {

                if (bs == Play.DOUBLE_DOWN && hand.size() == 2) {
                    dealer.doubleDown(this, this.hid);
                }
                if (bs == Play.HIT) {
                    dealer.hit(this, this.hid);
                }
                if (bs == Play.SPLIT) {
                    dealer.hit(this, this.hid);
                }
                if (bs == Play.DOUBLE_DOWN && hand.size() != 2) {
                    dealer.hit(this, this.hid);
                }
                if (bs == Play.STAY) {
                    dealer.stay(this, hid);
                }

                try {
                    Random random1 = new Random();
                    int r1 = random1.nextInt(4000);
                    while (r1 < 2000) {
                        r1 = random1.nextInt(4000);
                    }
                    Thread.sleep(r1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MyBot.class.getName()).log(Level.SEVERE, null, ex);
                }

                bs = r.get_advice();

            }
            if (this.hand.getValue() > 21) {
                endGame(shoeSize);
            } else {
                dealer.stay(this, hid);
            }
        }
    }

    /**
     * Private class to carry out the Strategy task.
     */
    private class Responder implements Runnable {

        private Dealer dealer;
        private Hand hand;
        private Card upcard;
        private Play advice;

        public Responder(Hand myhand, Card upcard, Dealer d) {
            this.hand = myhand;
            this.upcard = upcard;
            this.dealer = d;
        }

        public Play get_advice() {
            Thread t1 = new Thread(this);
            t1.start();
            return advice;
        }

        @Override
        public void run() {
            BasicStrategy b = new BasicStrategy();
            try {
                Random random2 = new Random();
                int r2 = random2.nextInt(4000);
                while (r2 < 2000) {
                    r2 = random2.nextInt(4000);
                }
                Thread.sleep(r2);
            } catch (InterruptedException ex) {
                Logger.getLogger(MyBot.class.getName()).log(Level.SEVERE, null, ex);
            }
            advice = b.advise(hand, upcard);
        }

    }

}
