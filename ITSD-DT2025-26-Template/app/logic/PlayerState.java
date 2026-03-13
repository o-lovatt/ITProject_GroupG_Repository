package logic;
import structures.PlayerSide;
import structures.basic.Unit;

public class PlayerState {
    //public static final int INITIAL_HEALTH = 20;
    public static final int INITIAL_MANA = 0;
    public static final int MAX_MANA = 9;

    private final PlayerSide side;
    private final Unit avatar; ///DOES THIS NEED TO BE AVATAR UNIT?
    //private int health; ???
    private int mana;
    //private final Deck deck; ///TODO uncomment when these classes ar ready
    //private final Hand hand;

    public PlayerState(PlayerSide side, Unit avatar) { /// add deck in again later
        this.side = side;
        this.avatar = avatar;
        //this.health = INITIAL_HEALTH;
        this.mana = INITIAL_MANA;
        //this.deck = deck;
        //this.hand = new Hand();
        //this.avatar = setOwnerState(this);//so avatar can reference its owner
        //is this needed? not sure if this syntax will work
    }


        //health stuff
        public int getHealth () {
            return avatar.getHealth();//returns avatars health
        }

        public void takeDamage ( int damage){
            avatar.takeDamage(damage);//damages avatar
        }

        public void heal ( int amount){
            avatar.heal(amount);//heals avatar
        }

        public boolean isDefeated () {
            return avatar.isDead();//true if health = 0
        }


        //mana stuff
        public int getMana () {
            return mana;//return current mana
        }

        public void setManaForTurn ( int turnNumber){
            this.mana = Math.min(turnNumber + 1, MAX_MANA); //set mana to turnNumber + 1 (max mana = 9)
        }

        public void useMana ( int cost){
            if (cost > mana) {
                System.out.println(side + " has insufficient mana. " + cost + " mana required.");//change to error exception!!
            } else {
                this.mana -= cost;
            }
        }


        public boolean hasMana(int cost) {
            return mana >= cost; //checks if player has enough mana first, return true if they do
        }

        public void resetMana() {
            this.mana = 0;//reset mana at END_TURN
        }

        /// TODO DRAW CARD STUFF

//        public Deck getDeck(){
//            return deck;
//        }
//
//        public Hand getHand(){
//            return hand;
//        }

        //get avatar
        public Unit getAvatar(){
            return avatar;
        }
        //get player side
        public PlayerSide getSide(){
            return side;
        }

        @Override
        public String toString(){
            return "PlayerState" + side + "HP: " + getHealth()  + "Mana: " + mana; // + "Hand: " + hand  + "Deck: " + deck;
        }
    }

