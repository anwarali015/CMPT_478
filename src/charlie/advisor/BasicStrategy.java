package charlie.advisor;

import charlie.card.Card;
import charlie.card.Hand;
import charlie.plugin.IAdvisor;
import charlie.util.Play;
import java.util.ArrayList;

public class BasicStrategy implements IAdvisor {

    private final int numbers[] = {17, 16, 15, 14, 13, 12, 11, 10, 9};
    ArrayList<ArrayList<Play>> numbers_list;

    private final int numbers1[] = {8, 7, 6, 5};
    ArrayList<ArrayList<Play>> numbers_list1;

    private final int acs[] = {21, 20, 19, 18, 17, 16, 15, 14, 13};
    ArrayList<ArrayList<Play>> acs_list;

    private final int pair[] = {16, 20, 18, 14, 12, 10, 8, 6, 4};
    ArrayList<ArrayList<Play>> pair_list;

    @Override
    public Play advise(Hand myHand, Card upCard) {
        // throw new UnsupportedOperationException("Not supported yet.");
        build();
        int dhand = upCard.value();

        if (myHand.isPair()) {
            //Go to the pair array 
            int sum = getsum(myHand.getCard(0).value());
            int index = getindex(pair, sum);
            return pair_list.get(index).get(dhand - 2);
        }else if (myHand.size()==2 && (myHand.getCard(0).getName().equals("A") || myHand.getCard(1).getName().equals("A"))){
            //Go to the ac's array
            int index = getindex(acs,myHand.getValue());
            return acs_list.get(index).get(dhand-2);
        }else if (myHand.getValue() >= 9 && myHand.getValue() <= 17) {
            //Go to number array 
            int index = getindex(numbers, myHand.getValue());
            return numbers_list.get(index).get(dhand - 2);
        } else if (myHand.getValue() >= 5 && myHand.getValue() <= 8) { 
            return Play.HIT;
        } else {
            return Play.STAY;
        } 
        
    }

    private void build() {
        System.out.println("Number Array");
        build_numbers();
        //build_numbers1();
        System.out.println("Ac's Array");
        build_acs();
        System.out.println("Pair Array");
        build_pair();
    }

    private int getsum(int hand) {
        return hand+hand;
    }

    //Build pair list
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

        for (int i = 0; i < pair_list.size(); i++) 
            System.out.println(pair[i] + ": " + pair_list.get(i));
        
    }

    // build acs list
    private void build_acs() {
        acs_list = new ArrayList<>();
        for (int i = 0; i < acs.length; i++) {
            acs_list.add(new ArrayList<Play>());
        }

        //Row 1-3 (A10-A8)
        for (int i = 0; i < 3; i++) {
            populate(i, 1, 10, Play.STAY, acs_list);
        }
        //Row 4 (A7)
        populate(3, 1, 1, Play.STAY, acs_list);
        populate(3, 2, 5, Play.DOUBLE_DOWN, acs_list);
        populate(3, 6, 7, Play.STAY, acs_list);
        populate(3, 8, 10, Play.HIT, acs_list);
        //Row 5 (A6)
        populate(4, 1, 1, Play.HIT, acs_list);
        populate(4, 2, 5, Play.DOUBLE_DOWN, acs_list);
        populate(4, 6, 10, Play.HIT, acs_list);
        //Row 6-7 (A5) (A4)
        for (int i = 5; i <= 6; i++) {
            populate(i, 1, 2, Play.HIT, acs_list);
            populate(i, 3, 5, Play.DOUBLE_DOWN, acs_list);
            populate(i, 6, 10, Play.HIT, acs_list);
        }
        //Row 8-9 (A3) (A2)
        for (int i = 7; i <= 8; i++) {
            populate(i, 1, 3, Play.HIT, acs_list);
            populate(i, 4, 5, Play.DOUBLE_DOWN, acs_list);
            populate(i, 6, 10, Play.HIT, acs_list);
        }

        for (int i = 0; i < acs_list.size(); i++) 
            System.out.println(acs[i] + ": " + acs_list.get(i));
        
    }

    //build number1 list
    private void build_numbers1() {
        numbers_list1 = new ArrayList<>();
        for (int i = 0; i < numbers1.length; i++) {
            numbers_list1.add(new ArrayList<Play>());
        }

        //Row 1(8)
        for (int i = 0; i < numbers1.length; i++) {
            populate(i, 1, 10, Play.HIT, numbers_list1);
        }

        for (int i = 0; i < numbers_list1.size(); i++) 
            System.out.println(numbers1[i] + ": " + numbers_list1.get(i));
        
    }

    //build numbers list
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

        for (int i = 0; i < numbers_list.size(); i++) 
            System.out.println(numbers[i] + ": " + numbers_list.get(i));
        

    }

    //populate the given list
    private void populate(int index, int start, int end, Play advise, ArrayList<ArrayList<Play>> list) {
        for (int i = start; i <= end; i++) {
            list.get(index).add(advise);
        }
    }

    //get index of the target in the array.
    private int getindex(int[] array, int target) {
        int temp = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                temp = i;
            }
        }
        return temp;
    }
    
    public static void main(String[] args) {
       BasicStrategy b = new BasicStrategy();
       b.build();
    }
}
