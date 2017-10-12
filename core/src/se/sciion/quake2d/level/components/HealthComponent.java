package se.sciion.quake2d.level.components;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.system.CollisionCallback;
import se.sciion.quake2d.level.system.SoundSystem;

/**
 * Add to entities which should take damage.
 * @author sciion
 *
 */
public class HealthComponent extends EntityComponent implements CollisionCallback {
	public final int MAX_HEALTH;
	public final int MAX_ARMOR;
	
	private int armor;
	private int health;
	private boolean hasBeenKilled;
	

    private TextureRegion amountTexture;
    private TextureRegion[][] amountBar;
	
    private Level level;
    
	public HealthComponent(int health, int maxArmor, TextureRegion amountTexture, Level level) {

		super();
		this.health = health;
		this.amountTexture = amountTexture;
        this.amountBar = amountTexture.split(48, 10);
		MAX_HEALTH = health;
		this.armor = 0;
		this.MAX_ARMOR = maxArmor;
		this.level = level;
	}

	@Override
	public void render(RenderModel batch) {
		if (batch.debugging) return; // No debug drawings for this thing.
		PhysicsComponent playerPhysics = getParent().getComponent(ComponentTypes.Physics);
		Vector2 playerPosition = playerPhysics.getBody().getPosition();

		float ratioHealthLeft = ratioHealth();
		float ratioArmorLeft  = ratioArmor();
		
		if (isDead()) return;

		batch.spriteRenderer.setColor(0.8f, 0.1f, 0.1f, 1.0f);
		batch.spriteRenderer.draw(amountBar[1][0], playerPosition.x - 0.5f, playerPosition.y - 0.7f, 0.0f, 0.0f,
		                          amountBar[1][0].getRegionWidth(), amountBar[0][0].getRegionHeight(),
		                          1.0f / 48.0f * ratioHealthLeft, 1.0f / 48.0f, 0.0f);
		batch.spriteRenderer.draw(amountBar[0][0], playerPosition.x - 0.5f, playerPosition.y - 0.7f, 0.0f, 0.0f,
		                          amountBar[0][0].getRegionWidth(), amountBar[0][0].getRegionHeight(),
		                          1.0f / 48.0f, 1.0f / 48.0f, 0.0f);

		batch.spriteRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);

		if (getArmor() == 0) return;

		batch.spriteRenderer.setColor(0.1f, 0.1f, 0.8f, 1.0f);
		batch.spriteRenderer.draw(amountBar[1][0], playerPosition.x - 0.5f, playerPosition.y - 1.0f, 0.0f, 0.0f,
		                          amountBar[1][0].getRegionWidth(), amountBar[0][0].getRegionHeight(),
		                          1.0f / 48.0f * ratioArmorLeft, 1.0f / 48.0f, 0.0f);

		batch.spriteRenderer.draw(amountBar[0][0], playerPosition.x - 0.5f, playerPosition.y - 1.0f, 0.0f, 0.0f,
		                          amountBar[0][0].getRegionWidth(), amountBar[0][0].getRegionHeight(),
		                          1.0f / 48.0f, 1.0f / 48.0f, 0.0f);

		batch.spriteRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public boolean isDead() {
		return hasBeenKilled;
	}
	
	public void remove(float f, Entity responsible){
		armor -= f;
		if(armor < 0){
			health = MathUtils.clamp(health + armor, 0, health);
			armor = 0;
		}
		
		BotInputComponent input = parent.getComponent(ComponentTypes.BotInput);
		BotInputComponent input2 = responsible.getComponent(ComponentTypes.BotInput);
		if(input2 != null) {
			if(input == null) {
				// Only count for small damage when done on non-player
				level.getStats().recordDamageTaken(input2.getBehaviourTree(), null, f * 0.2f);

			}
			else{
				level.getStats().recordDamageTaken(input2.getBehaviourTree(), input.getBehaviourTree(), f);
				if(health <= 0) {
					level.getStats().recordKill(input2.getBehaviourTree());
					//level.getStats().recordSurvivior(input2.getBehaviourTree());
				}
			}
			
		}

		PhysicsComponent physics = parent.getComponent(ComponentTypes.Physics);
		SoundSystem.getInstance().playSound("hit", physics.getBody().getPosition(), 1.0f);
	}

	public int getHealth() {
		return health;
	}
	
	public int getArmor(){
		return armor;
	}
	
	public int getProtection(){
		return health + armor;
	}
	
	public float ratioHealth(){
		return health / (float)(MAX_HEALTH);
	}
	
	public float ratioArmor(){
		return armor / (float)(MAX_ARMOR);
	}
	
	public void addHealth(int amount) {
		health = MathUtils.clamp(health + amount, 0, MAX_HEALTH);
	}
	
	public void addArmor(int amount) {
		armor = MathUtils.clamp(armor + amount, 0, MAX_ARMOR);
	}
	
	@Override
	public void tick(float delta) {
		if (health <= 0 && hasBeenKilled == false) {
			parent.setActive(false);
			hasBeenKilled = true;
			parent.setActive(false);
		}
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Health;
	}

	@Override
	public void process(Entity target) {

		DamageComponent damage = target.getComponent(ComponentTypes.Damage);
		if(damage != null){
			remove(damage.getDamage(), damage.getResponsible());
		}
	}

}
