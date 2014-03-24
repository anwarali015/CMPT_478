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
            new Hand(hid);
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
        System.out.println("BUST FOR " + hid.getSeat());
    }

    @Override
    public void win(Hid hid) {
        System.out.println("WIN FOR " + hid.getSeat());
    }

    @Override
    public void blackjack(Hid hid) {
        System.out.println("B-J FOR " + hid.getSeat());
    }

    @Override
    public void charlie(Hid hid) {
        System.out.println("CHARLIE FOR " + hid.getSeat());
    }

    @Override
    public void lose(Hid hid) {
        System.out.println("LOSE FOR " + hid.getSeat());
    }

    @Override
    public void push(Hid hid) {
        System.out.println("PUSH FOR " + hid.getSeat());
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
        System.out.println("INSIDE RUN FOR " + hid.getSeat());
        synchronized (this.dealer) {

            Responder r = new Responder(this.hand, this.upCard, this.dealer);

            Play play = r.get_advice();

            while (play != Play.STAY) {

                if (hand.getValue() > 21) {
                    return;
                } else if (hand.getValue() >= 17 && hand.getValue() <= 21) {
                    //I know to stay here, but wanna sleep 
                    //so it looks like I had to think to stay.
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MyBot.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dealer.stay(this, hid);
                    System.out.println("I CHOSE TO STAY");
                    break;
                }
                if (play == Play.DOUBLE_DOWN && hand.size() == 2) {
                    dealer.doubleDown(this, this.hid);
                    break;
                }

                if (play == Play.DOUBLE_DOWN && hand.size() != 2) {
                    dealer.stay(this, this.hid);
                    break;
                }

                if (play == Play.HIT) {
                    dealer.hit(this, this.hid);
                    if (hand.getValue() > 21) {
                        System.out.println("I HIT, SO I BUST");
                       return;
                    }
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

                play = r.get_advice();

            }

            if (play == Play.STAY) {
                dealer.stay(this, hid);
                return;
            }

            if (hand.getValue() > 21 || play != Play.STAY) {
                return;
            }
            
            
            if (play != Play.DOUBLE_DOWN && play != Play.STAY) {
                if (hand.getValue() > 21) {
                    endGame(shoeSize);
                    return;
                } else {
                    dealer.stay(this, hid);
                    return;
                }
            }

        }
    }

    /**
     * Private class to carry out the Strategy task.
     */
    private class Responder implements Runnable {

        private Dealer dealer;
        private Hand bsHand;
        private Card upcard;
        private Play advice;

        public Responder(Hand myhand, Card upcard, Dealer d) {
            this.bsHand = myhand;
            this.upcard = upcard;
            this.dealer = d;
        }

        public Play get_advice() {
            System.out.println("INSIDE ADVICE FOR " + hid.getSeat());
            System.out.println("HAND VALUE " + bsHand.getValue());
            Thread t1 = new Thread(this);
            t1.start();
            System.out.println("POSSIBLE ADVICE " + advice);
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
