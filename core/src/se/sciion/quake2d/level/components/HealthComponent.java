package se.sciion.quake2d.level.components;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.system.CollisionCallback;

/**
 * Add to entities which should take damage.
 * @author sciion
 *
 */
public class HealthComponent extends EntityComponent implements CollisionCallback{

	

	public final int MAX_HEALTH;
	public final int MAX_ARMOR;
	
	private int armor;
	private int health;
	
    private TextureRegion amountTexture;
    private TextureRegion[][] amountBar;
	
	public HealthComponent(int health, int maxArmor, TextureRegion amountTexture) {

		super();
		this.health = health;
		this.amountTexture = amountTexture;
        this.amountBar = amountTexture.split(48, 10);
		MAX_HEALTH = health;
		this.armor = 0;
		this.MAX_ARMOR = maxArmor;
	}

	@Override
	public void render(RenderModel batch) {
		PhysicsComponent playerPhysics = getParent().getComponent(ComponentTypes.Physics);
		Vector2 playerPosition = playerPhysics.getBody().getPosition();

		float ratioHealthLeft = health / (float)(MAX_HEALTH);
		// TODO: change this later to consider armor amounts.
		float ratioArmorLeft  = armor / (float)(MAX_ARMOR);

		if (health == 0) return; // It's dead Jim!

		batch.spriteRenderer.setColor(0.8f, 0.1f, 0.1f, 1.0f);
		batch.spriteRenderer.draw(amountBar[1][0], playerPosition.x - 0.5f, playerPosition.y - 0.7f, 0.0f, 0.0f,
		                          amountBar[1][0].getRegionWidth(), amountBar[0][0].getRegionHeight(),
		                          1.0f / 48.0f * ratioHealthLeft, 1.0f / 48.0f, 0.0f);
		batch.spriteRenderer.draw(amountBar[0][0], playerPosition.x - 0.5f, playerPosition.y - 0.7f, 0.0f, 0.0f,
		                          amountBar[0][0].getRegionWidth(), amountBar[0][0].getRegionHeight(),
		                          1.0f / 48.0f, 1.0f / 48.0f, 0.0f);

		batch.spriteRenderer.setColor(0.8f, 0.8f, 0.8f, 1.0f);
		batch.spriteRenderer.draw(amountBar[1][0], playerPosition.x - 0.5f, playerPosition.y - 1.0f, 0.0f, 0.0f,
		                          amountBar[1][0].getRegionWidth(), amountBar[0][0].getRegionHeight(),
		                          1.0f / 48.0f * ratioArmorLeft, 1.0f / 48.0f, 0.0f);

		batch.spriteRenderer.draw(amountBar[0][0], playerPosition.x - 0.5f, playerPosition.y - 1.0f, 0.0f, 0.0f,
		                          amountBar[0][0].getRegionWidth(), amountBar[0][0].getRegionHeight(),
		                          1.0f / 48.0f, 1.0f / 48.0f, 0.0f);
		batch.spriteRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public void remove(int amount){
		armor -= amount;
		if(armor < 0){
			health = MathUtils.clamp(health + armor, 0, health);
			armor = 0;
		}
		
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
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Health;
	}

	@Override
	public void process(Entity target) {

		DamageComponent damage = target.getComponent(ComponentTypes.Damage);
		if(damage != null){
			remove(damage.getDamage());
		}
	}

}
