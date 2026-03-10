package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import scala.xml.dtd.DEFAULT;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	int id;
	UnitAnimationType animation;
	Position position;
	UnitAnimationSet animations;
	ImageCorrection correction;

    private int health;
    private int maxHealth;
    private int attackPower;
    private boolean hasMoved;
    private boolean hasAttacked;//2 booleans added

    private static final int DEFAULT_HEALTH = 20;
    private static final int DEFAULT_ATTACK = 2;
	
	public Unit() {}

    public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
        super();
        this.id = id;
        this.animation = UnitAnimationType.idle;
        this.position = new Position(0, 0, 0, 0);
        this.correction = correction;
        this.animations = animations;
        initCombatStats(DEFAULT_HEALTH, DEFAULT_ATTACK);
    }

    public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
        super();
        this.id = id;
        this.animation = UnitAnimationType.idle;
        this.position = new Position(currentTile.getXpos(), currentTile.getYpos(), currentTile.getTilex(), currentTile.getTiley());
        this.correction = correction;
        this.animations = animations;
        initCombatStats(DEFAULT_HEALTH, DEFAULT_ATTACK);
    }

    public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations, ImageCorrection correction) {
        super();
        this.id = id;
        this.animation = animation;
        this.position = position;
        this.animations = animations;
        this.correction = correction;
        initCombatStats(DEFAULT_HEALTH, DEFAULT_ATTACK);
    }


    private void initCombatStats(int health, int attackPower) {
        //initial combat stats
        this.health = health;
        this.maxHealth = health;
        this.attackPower = attackPower;
        this.hasMoved = false;
        this.hasAttacked = false;
    }
        //attacking getters and setters
        public int getHealth(){
            return health;
        }
        public int getMaxHealth(){
            return maxHealth;
        }
        public void setMaxHealth(int maxHealth) {
            this.maxHealth = maxHealth; //minor change from getMaxHealth -> maxHealth
        }

        public void takeDamage(int damage){
            this.health = Math.max(0, this.health - damage);
        }
        public void heal(int amount){
            this.health = Math.min(this.maxHealth, this.health + amount);
        }

        //attack getter and setter
        public int getAttackPower() {
            return attackPower;
        }
        public void setAttackPower(int  attackPower) {
            this.attackPower = attackPower;
        }

        public boolean isDead() {
            return health <= 0;
        }

        //turn getters and setters
        public boolean hasMoved() {
            return hasMoved; //minor fix removed ()
        }
        public void setHasMoved(boolean hasMoved) {
            this.hasMoved = hasMoved;
        }
        public boolean hasAttacked() {
            return hasAttacked;
        }
        public void setHasAttacked(boolean hasAttacked) {
            this.hasAttacked = hasAttacked;
        }
        public void resetTurnState() {
            this.hasMoved = false;
            this.hasAttacked = false;//resets turn flags
        }



	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UnitAnimationType getAnimation() {
		return animation;
	}
	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}
	
	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
	}
}
