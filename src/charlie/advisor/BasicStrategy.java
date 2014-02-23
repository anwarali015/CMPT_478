package charlie.advisor;

import charlie.card.Card;
import charlie.card.Hand;
import charlie.plugin.IAdvisor;
import charlie.util.Play;
import java.util.ArrayList;

/**
 *
 * @author Mohammed Ali, Dan Blossom, Joseph Muro.
 * Assignment: Charlie Advisor Plugin.
 *
 */
public class BasicStrategy implements IAdvisor {

    private final int numbers[] = {17, 16, 15, 14, 13, 12, 11, 10, 9};
    ArrayList<ArrayList<Play>> numbers_list;

    private final int[] ace = {21, 20, 19, 18, 17, 16, 15, 14, 13};
    ArrayList<ArrayList<Play>> ace_list;

    private final int pair[] = {16, 20, 18, 14, 12, 10, 8, 6, 4};
    ArrayList<ArrayList<Play>> pair_list;

    /**
     * Advise for the player
     * 
     * @param myHand the hand of the player
     * @param upCard the dealer hand value
     * @return advise according to the strategy card.
     */
    @Override
    public Play advise(Hand myHand, Card upCard) {
        // throw new UnsupportedOperationException("Not supported yet.");
        //Build and initilize the array.
        build();
        // -2 on the dealer's upCard will give
        // exact index value in the list.
        int dhand = upCard.value() - 2;
        Play advise = null;
        if (myHand.isPair()) {
            //Go to the pair array 
            int sum;
            if (contains_ace(myHand)) {
                //Possibility of Double Ace's
                sum = get_sum(myHand);
                if (sum == 12 || sum == 2) {
                    //Two possible values of an ace pair
                    advise = ace_list.get(0).get(dhand);
                }
                
            } else {
                //Not an ace so just perform the usual taskd
                sum = get_sum(myHand);
                int index = get_index(pair, sum);
                advise = pair_list.get(index).get(dhand);
            }

        } else if (myHand.size() == 2 && contains_ace(myHand)) {
            //Go to the ac's array
            int index = get_index(ace, myHand.getValue());
            advise = ace_list.get(index).get(dhand);
        } else if (myHand.getValue() >= 9 && myHand.getValue() <= 17) {
            //Go to number array 
            int index = get_index(numbers, myHand.getValue());
            advise = numbers_list.get(index).get(dhand);
        } else if (myHand.getValue() >= 5 && myHand.getValue() <= 8) {
            //Always hit between 5-8 inclusive
            advise = Play.HIT;
        } else {
            // Always Stay when 17+
            advise = Play.STAY;
        }

        return advise;
    }

    /**
     * Builds and populates array.
     */
    private void build() {
        build_numbers();
        build_acs();
        build_pair();
    }

    /**
     * Checks the hand for and ace.
     *
     * @param myhand the hand to check for an ace
     * @return true if the hand contains an ace, false otherwise.
     */
    private boolean contains_ace(Hand myhand) {
        boolean found = false;
        for (int i = 0; i < myhand.size(); i++) {
            if (myhand.getCard(0).isAce()) {
                found = true;
            }
        }
        return found;
    }

    /**
     *Total value of the hand
     * 
     * @param hand to computer the sum
     * @return the sum of the hand
     */
    private int get_sum(Hand hand) {
        int total = 0;
        for (int i = 0; i < hand.size(); i++) {
            total += hand.getCard(i).value();
        }
        return total;
    }

    /**
     * Builds and populates the pair array.
     */
    private void build_pair() {
        pair_list = new ArrayList<>();
        for (int i = 0; i < pair.length; i++) {
            pair_list.add(new ArrayList<Play>());
        }

        //Row1 (AA,88)
        populate(0, 1, 10, Play.SPLIT, pair_list);
        //Row2 (10,10)
        populate(1, 1, 10, Play.STAY, pair_list);
        //Row3(9,9)
        populate(2, 1, 5, Play.SPLIT, pair_list);
        populate(2, 6, 6, Play.STAY, pair_list);
        populate(2, 7, 8, Play.SPLIT, pair_list);
        populate(2, 9, 10, Play.STAY, pair_list);
        //Row4 (7,7)
        populate(3, 1, 6, Play.SPLIT, pair_list);
        populate(3, 7, 10, Play.HIT, pair_list);
        //Row5 (6,6)
        populate(4, 1, 5, Play.SPLIT, pair_list);
        populate(4, 6, 10, Play.HIT, pair_list);
        //Row6 (5,5)
        populate(5, 1, 8, Play.DOUBLE_DOWN, pair_list);
        populate(5, 9, 10, Play.HIT, pair_list);
        //Row7 (4,4)
        populate(6, 1, 3, Play.HIT, pair_list);
        populate(6, 4, 5, Play.SPLIT, pair_list);
        populate(6, 6, 10, Play.HIT, pair_list);
        //Row8-9 (3,3) (2,2)
        for (int i = 7; i <= 8; i++) {
            populate(i, 1, 6, Play.SPLIT, pair_list);
            populate(i, 7, 10, Play.HIT, pair_list);
        }
    }

    /**
     * Builds the populates the ace's array.
     */
    private void build_acs() {
        ace_list = new ArrayList<>();
        for (int i = 0; i < ace.length; i++) {
            ace_list.add(new ArrayList<Play>());
        }

        //Row 1-3 (A10-A8)
        for (int i = 0; i < 3; i++) {
            populate(i, 1, 10, Play.STAY, ace_list);
        }
        //Row 4 (A7)
        populate(3, 1, 1, Play.STAY, ace_list);
        populate(3, 2, 5, Play.DOUBLE_DOWN, ace_list);
        populate(3, 6, 7, Play.STAY, ace_list);
        populate(3, 8, 10, Play.HIT, ace_list);
        //Row 5 (A6)
        populate(4, 1, 1, Play.HIT, ace_list);
        populate(4, 2, 5, Play.DOUBLE_DOWN, ace_list);
        populate(4, 6, 10, Play.HIT, ace_list);
        //Row 6-7 (A5) (A4)
        for (int i = 5; i <= 6; i++) {
            populate(i, 1, 2, Play.HIT, ace_list);
            populate(i, 3, 5, Play.DOUBLE_DOWN, ace_list);
            populate(i, 6, 10, Play.HIT, ace_list);
        }
        //Row 8-9 (A3) (A2)
        for (int i = 7; i <= 8; i++) {
            populate(i, 1, 3, Play.HIT, ace_list);
            populate(i, 4, 5, Play.DOUBLE_DOWN, ace_list);
            populate(i, 6, 10, Play.HIT, ace_list);
        }
    }

    /**
     * Builds and populates the numbers array.
     */
    private void build_numbers() {
        numbers_list = new ArrayList<>();
        for (int i = 0; i < numbers.length; i++) {
            numbers_list.add(new ArrayList<Play>());
        }

        //Row1 (17)
        populate(0, 1, 10, Play.STAY, numbers_list);
        //Row2 - Row5 (16-13)
        for (int i = 0; i < 4; i++) {
            populate(i + 1, 1, 5, Play.STAY, numbers_list);
            populate(i + 1, 6, 10, Play.HIT, numbers_list);
        }
        //Row 6 (12)
        populate(5, 1, 2, Play.HIT, numbers_list);
        populate(5, 3, 5, Play.STAY, numbers_list);
        populate(5, 6, 10, Play.HIT, numbers_list);
        //Row 7 (11)
        populate(6, 1, 9, Play.DOUBLE_DOWN, numbers_list);
        populate(6, 10, 10, Play.HIT, numbers_list);
        //Row 8 (10)
        populate(7, 1, 8, Play.DOUBLE_DOWN, numbers_list);
        populate(7, 9, 10, Play.HIT, numbers_list);
        //Row 9 (9)
        populate(8, 1, 1, Play.HIT, numbers_list);
        populate(8, 2, 5, Play.DOUBLE_DOWN, numbers_list);
        populate(8, 6, 10, Play.HIT, numbers_list);
    }

    /**
     * Populates the given array using the given advise.
     *
     * @param index the index of the array
     * @param start the index to start
     * @param end the index to end
     * @param advise the advise to allocate
     * @param list the arraylist to add the advise to.
     */
    private void populate(int index, int start, int end, Play advise, ArrayList<ArrayList<Play>> list) {
        for (int i = start; i <= end; i++) {
            list.get(index).add(advise);
        }

    }

    /**
     * Index of the specified value in the
     * specified array
     *
     * @param array the array to find the index in.
     * @param target the index of this target
     * @return the index of the target
     */
    private int get_index(int[] array, int target) {
        int temp = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                temp = i;
            }
        }

        return temp;
    }
}
