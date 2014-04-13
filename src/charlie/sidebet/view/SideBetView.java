package charlie.sidebet.view;

import charlie.audio.Effect;
import charlie.audio.SoundFactory;
import charlie.card.Hid;
import charlie.plugin.ISideBetView;
import charlie.view.AMoneyManager;
import charlie.view.sprite.Chip;
import charlie.view.sprite.ChipButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * This class implements the side bet view
 *
 * @author Ron Coleman, Ph.D.
 */
public class SideBetView implements ISideBetView {
    // private final Logger LOG = LoggerFactory.getLogger(SideBetView.class);

    public final static int X = 400;
    public final static int Y = 200;
    public final static int DIAMETER = 50;

    protected Font font = new Font("Arial", Font.BOLD, 18);
    protected BasicStroke stroke = new BasicStroke(3);

    // See http://docs.oracle.com/javase/tutorial/2d/geometry/strokeandfill.html
    protected float dash1[] = {10.0f};
    protected BasicStroke dashed
            = new BasicStroke(3.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, dash1, 0.0f);

    protected List<ChipButton> buttons;
    protected int amt = 0;
    protected AMoneyManager moneyManager;

    //added stuff
    protected List<Chip> chips = new ArrayList<>();
    public final static String FIVE = "./images/chip-5-1.png";
    public final static String TWENTYFIVE = "./images/chip-25-1.png";
    public final static String HUNDRED = "./images/chip-100-1.png";
    protected final String super7 = "Super 7        pays    3:1";
    protected final String exactly13 = "Exactly 13    pays  10:1";
    protected final String royalMatch = "Royal Match pays  25:1";
    protected Image img;

    public SideBetView() {
        //LOG.info("side bet view constructed");

    }

    /**
     * Sets the money manager.
     *
     * @param moneyManager
     */
    @Override
    public void setMoneyManager(AMoneyManager moneyManager) {
        this.moneyManager = moneyManager;
        this.buttons = moneyManager.getButtons();
    }

    /**
     * Registers a click for the side bet.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    @Override
    public void click(int x, int y) {

        // Test if any chip button has been pressed.
        for (ChipButton button : buttons) {
            if (button.isPressed(x, y)) {
                SoundFactory.play(Effect.CHIPS_IN);
                amt += button.getAmt();
               // LOG.info("A. side bet amount "+button.getAmt()+" updated new amt = "+amt);

                //Added
                int n = chips.size();
                int xStart = (X - DIAMETER / 2) + 60;
                int yStart = (Y - DIAMETER / 2);
                Random ran = new Random();
                img = getChipImage(button.getAmt());
                int placeX = xStart + n * (img.getWidth(null)) / 3 + ran.nextInt(10) - 10;
                int placeY = yStart + ran.nextInt(5) - 5;
                Chip chip = new Chip(img, placeX, placeY, button.getAmt());
                chips.add(chip);
                //end added
            }
        }

        //Tests if sidebet has been cleared
        if (inRange(x, y)) {
            SoundFactory.play(Effect.CHIPS_OUT);
            amt = 0;
            // LOG.info("B. side bet amount cleared");
            chips.clear();
        }
    }

    /**
     * Informs view the game is over and it's time to update the bankroll for
     * the hand.
     *
     * @param hid Hand id
     */
    @Override
    public void ending(Hid hid) {
        double bet = hid.getSideAmt();
        
        if (bet == 0) {
            return;
        }
        //LOG.info("side bet outcome = "+bet);
        // Update the bankroll
        moneyManager.increase(bet);
        // LOG.info("new bankroll = "+moneyManager.getBankroll());
    }

    /**
     * Informs view the game is starting
     */
    @Override
    public void starting() {
    }

    /**
     * Gets the side bet amount.
     *
     * @return Bet amount
     */
    @Override
    public Integer getAmt() {
        return amt;
    }

    /**
     * Updates the view
     */
    @Override
    public void update() {
    }

    /**
     * Renders the view
     *
     * @param g Graphics context
     */
    @Override
    public void render(Graphics2D g) {
        // Draw the at-stake place on the table
        g.setColor(Color.RED);
        g.setStroke(dashed);
        g.drawOval(X - DIAMETER / 2, Y - DIAMETER / 2, DIAMETER, DIAMETER);

        //Sidebet payout label / text
        Font ruleFont = new Font("Ariel", Font.PLAIN, 11);
        g.setColor(Color.YELLOW);
        g.setFont(ruleFont);

        g.drawString(super7, X + 33, Y - 12);
        g.drawString(exactly13, X + 33, Y);
        g.drawString(royalMatch, X + 33, Y + 12);
        
        // Draw the at-stake amount
        FontMetrics fm = g.getFontMetrics(font);
        String text = "" + amt;
        int x = X - fm.charsWidth(text.toCharArray(), 0, text.length()) / 2;
        int y = Y + fm.getHeight() / 4;
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("" + amt, x, y);
        for (Chip chip : chips) {
            chip.render(g);
        }
        
        // Draw the Result
        //Font result = new Font("Ariel",Font.BOLD,100);
        //g.setBackground(Color.RED);
        //g.setColor(Color.white);
        //g.setFont(ruleFont);
        //g.drawString("WIN", X+66, Y-12);
        
    }
    
    /**
     * Renders the WIN text on the table
     *  
     * @param name, the text to render
     * @param g 
     */
    private void draw_lose(String name, Graphics2D g){
        Font result = new Font("Ariel", Font.BOLD, 20);
        g.setFont(result);
        g.setColor(Color.RED);
         g.fillRect(X + 60, Y - 20, 60, 25); 
        g.setColor(Color.WHITE);
        g.drawString(name, X + 60, Y);
    }

    /**
     * Renders the WIN text on the table
     *  
     * @param name, the text to render
     * @param g 
     */
    private void draw_win(String name, Graphics2D g) {

        Font result = new Font("Ariel", Font.BOLD, 20);
        g.setFont(result);
        g.setColor(Color.GRAY);
        // g.drawRect(X+66,Y-12,50,30);
        // g.drawString(name, X + 70, Y + 2);
        // g.drawRect(X+70, Y-3, 50, 30);
        g.fillRect(X + 60, Y - 20, 45, 25); 
        g.setColor(Color.BLACK);
        g.drawString(name, X + 60, Y);
        
    }

    private boolean inRange(int x, int y) {
        //gets the x and y start position of sidebet oval
        int xStart = (X - DIAMETER / 2);
        int yStart = (Y - DIAMETER / 2);

        //if we are within the oval return true. Formula from AtStakeSprite
        return (x > xStart && x < xStart + DIAMETER && y > yStart && y < yStart + DIAMETER);
    }

    private Image getChipImage(int chipAmt) {

        Image img = null;
        switch (chipAmt) {

            case 5:
                img = (new ImageIcon(FIVE).getImage());
                break;

            case 25:
                img = (new ImageIcon(TWENTYFIVE).getImage());
                break;

            case 100:
                img = (new ImageIcon(HUNDRED).getImage());
                break;
        }

        return img;
    }
}
