import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Deck.java
 *
 * @brief A class that represents a deck of playing cards
 *
 * @details
 * This class is responsible for shuffling and dealing a deck of cards. The deck should
 * contain 52 cards A,2 - 10, J,Q K or four suits, but no jokers.
 */


class Deck
{
    private ArrayList<Card> pokerDeck = new ArrayList<Card>(); // the list of deck
    

    // Constructor which initialize the the deck array
    //
    // The array does not follow the increasing order
    public Deck()
    {
        System.out.printf("\u2666");
        for (Card.Suit suit : Card.Suit.values()){
            for (Card.Value value : Card.Value.values()){
                if (!value.equals(Card.Value.JOKER)){
                    Card card = new Card();
                    card.set_value(value, suit);
                    pokerDeck.add(card);
                }
            }
        }
    }
    

    // Take number of sets and the number of cards each set contains as input
    // Print one set per line
    public void deal_hand( int sets, int cards )
    {
        for (int u = 0; u < sets; u++){
            System.out.print("The " + (u + 1) + " draw: ");
            for (int v = 0; v < cards; v++){
                pokerDeck.get(u * sets + v).print();
            }
            System.out.println();
        }

    }

    // Visualize the current deck and print it in the standrd output
    public void print_deck()
    {
        for (Card printCard : pokerDeck){
            printCard.print();
        }
    }

    // Take seed value as parameter and shuffle the deck randomly using the seed value
    public void shuffle( int seed ) { Collections.shuffle(pokerDeck, new Random(seed)); }

    
    // Using bucket sort and insertion sort to sort the deck in increasing order
    // The suits are valued from highest to lowest: Spades, Hearts, Clubs, Diamonds
    // The cards are valued from highest to lowest: King,Queen,Jack,10,9,8,7,6,5,4,3,2,Ace
    // The sortPoker will be the deck after being sorted
    public void sort()
    {
       ArrayList<ArrayList<Card>> sortPoker = new ArrayList<ArrayList<Card>>();
       for (int a  = 0; a < Card.Suit.values().length; a++){
           sortPoker.add(new ArrayList<>());
       }

       for (int i = 0; i < pokerDeck.size(); i++){
           if (pokerDeck.get(i).get_suit().ordinal() == 3){
               sortPoker.get(0).add(pokerDeck.get(i));
           }
           if (pokerDeck.get(i).get_suit().ordinal() == 2){
               sortPoker.get(1).add(pokerDeck.get(i));
           }
           if (pokerDeck.get(i).get_suit().ordinal() == 1){
               sortPoker.get(2).add(pokerDeck.get(i));
           }
           if (pokerDeck.get(i).get_suit().ordinal() == 0){
               sortPoker.get(3).add(pokerDeck.get(i));
           }
       }

       for (int x = 0; x < sortPoker.size(); x++){
           for (int y = 1; y < sortPoker.get(x).size(); y++){
               Card current = sortPoker.get(x).get(y);
               int z = y - 1;

               while (z >= 0 && sortPoker.get(x).get(z).is_greater_than(current)){
                    sortPoker.get(x).set(z + 1, sortPoker.get(x).get(z));
                    z--;
               }
               sortPoker.get(x).set(z + 1, current);
           }
       }
       pokerDeck.clear();
       for (int e = 0; e < sortPoker.size(); e++){
           pokerDeck.addAll(sortPoker.get(e));
       }

    }

}
