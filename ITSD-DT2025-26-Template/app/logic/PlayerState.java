package logic;

public class PlayerState {
    public static final int INITIAL_HEALTH = 20;
    public static final int INITIAL_MANA = 0;
    public static final int MAX_MANA = 9;

    private final PlayerSide side;
    private final AvatarUnit avatar;
    private int mana;
    private final Deck deck;
    private final Hand hand;

    public PlayerState(PlayerSide side, AvatarUnit avatar, Deck deck) {
        this.side = side;
        this.avatar = avatar;
        this.mana = INITIAL_MANA;
        this.deck = deck;
        this.hand = new Hand();
        this.avatar = setOwnersate(this);//so avatar can reference its owner



        //health stuff
        public int getHealth() {
            return avatar.getCurrentHealth();//returns avatars health
        }

        public void takeDamage(int damage){
            avatar.takeDamage(damage);//damages avatar
        }

        public void heal(int amount) {
            avatar.heal(amount);//heals avatar
        }

        public boolean isDefeated() {
            return avatar.isDead();//true if health = 0
        }


        //mana stuff
        public int getMana() {
            return mana;//return current mana
        }

        public void setManaForTurn(int turnNumber){
            this.mana = Math.min(turnNumber + 1, MAX_MANA); //set mana to turnNumber + 1 (max mana = 9)
        }

        public void useMana(int cost) {
            if (cost > mana) {
                print(side + "has insufficient mana." + cost + "mana required.");//change to error exception!!
            }else {
                this.mana -= cost;
            }
        }

        public boolean hasMana(int cost) {
            return mana >= cost; //checks if player has enough mana first, return true if they do
        }

        public void resetMana() {
            this.mana = 0;//reset mana at END_TURN
        }

        //card deck stuff






    }
}
