package se.sciion.quake2d.level.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.utils.Array;

import se.sciion.quake2d.enums.ComponentTypes;
import se.sciion.quake2d.graphics.RenderModel;
import se.sciion.quake2d.level.Entity;
import se.sciion.quake2d.level.Level;
import se.sciion.quake2d.level.items.Weapon;
import se.sciion.quake2d.level.system.PhysicsSystem;

/**
 * Keep track of cooldowns related to weapons.
 * 
 * @author sciion
 *
 */
public class WeaponComponent extends EntityComponent {

	private float cooldown;
	private TextureRegion bulletTexture;

    private TextureRegion muzzleTexture;
	private PhysicsSystem physicsSystem;
	private Level level;

	public WeaponComponent(Level level, PhysicsSystem physicsSystem, TextureRegion bulletTexture, TextureRegion muzzleTexture) {
		cooldown = 0;

		// Load the bullet texture here. A bit ugly, maybe do this in the level instead?
		bulletTexture = new TextureRegion(new Texture(Gdx.files.internal("images/bullet.png")));


		this.bulletTexture = bulletTexture;
        this.muzzleTexture = muzzleTexture;
		this.physicsSystem = physicsSystem;
		this.level = level;
	}

	@Override
	public void render(RenderModel batch) {
		if (batch.debugging) return; // No debug render for this thing.
		SheetComponent spriteSheet = getParent().getComponent(ComponentTypes.Sheet);
		if (spriteSheet == null) return;

		InventoryComponent inventory = getParent().getComponent(ComponentTypes.Inventory);
		Array<Weapon> weapons = inventory.getItems(Weapon.class);

		if (weapons.size >= 1) {
			Weapon currentWeapon = weapons.first();

			if (currentWeapon.cooldown - cooldown <= 0.05) {
				PhysicsComponent playerPhysics = getParent().getComponent(ComponentTypes.Physics);
				Vector2 playerPosition = playerPhysics.getBody().getPosition();
				float playerAngle = playerPhysics.getBody().getAngle() * MathUtils.radiansToDegrees - 90.0f;
				playerPosition.add(new Vector2(-0.2f, 0.6f).rotate(playerAngle));
				batch.spriteRenderer.draw(muzzleTexture, playerPosition.x, playerPosition.y, 0.0f, 0.0f, muzzleTexture.getRegionWidth(),
				                          muzzleTexture.getRegionHeight(), 1.0f / 48.0f, 1.0f / 48.0f, playerAngle);
			}

			if (currentWeapon.getTag().equals("shotgun"))
				spriteSheet.setCurrentRegion("gun");
			else if (currentWeapon.getTag().equals("rifle"))
				spriteSheet.setCurrentRegion("silencer");
			else if (currentWeapon.getTag().equals("sniper"))
				spriteSheet.setCurrentRegion("machine");
		} else spriteSheet.setCurrentRegion("stand");
	}

	@Override
	public void tick(float delta) {
		cooldown -= delta;
		if (cooldown < 0) {
			cooldown = 0;
		}
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Weapon;
	}

	// Used for now to fire weapon. Should perhaps be internal logic
	public boolean fire(Vector2 heading, Vector2 origin) {
		if (cooldown <= 0.0f) {
			InventoryComponent inventory = getParent().getComponent(ComponentTypes.Inventory);
			if (inventory == null)
				return false;

			Array<Weapon> weapons = inventory.getItems(Weapon.class);
			if (weapons.size >= 1) {
				Weapon currentWeapon = weapons.first();

				// Create bullets
				for (int i = 0; i < currentWeapon.bullets; i++) {
					Vector2 bulletHeading = heading.cpy();
					float angle = bulletHeading.angle();
					bulletHeading.setAngle(
							angle + MathUtils.random(-currentWeapon.spread / 2.0f, currentWeapon.spread / 2.0f));
					{
						float x = origin.x + bulletHeading.x / 2.0f;
						float y = origin.y + bulletHeading.y / 2.0f;
						Entity e = level.createEntity();
						CircleShape circle = new CircleShape();
						circle.setRadius(0.13f);
						PhysicsComponent bulletPhysics = physicsSystem.createComponent(x, y, BodyType.DynamicBody,
								circle, true);
						bulletPhysics.getBody().setLinearDamping(0.0f);
						bulletPhysics.getBody().setAngularDamping(100.0f);

						e.addComponent(bulletPhysics);

						ProjectileComponent projectile = new ProjectileComponent(
								bulletHeading.cpy().scl(currentWeapon.speed));
						e.addComponent(projectile);


						SpriteComponent bulletSprite = new SpriteComponent(bulletTexture, new Vector2(0.0f, 0.0f), new Vector2(-0.12f, -0.12f),
						                                                   new Vector2(1.0f / 64.0f, 1.0f / 64.0f), 0.0f);
						e.addComponent(bulletSprite);

						DamageBoostComponent boost = parent.getComponent(ComponentTypes.Boost);
						float boostScl = 1;
						if(boost != null)
							boostScl = boost.boost;
						
						DamageComponent damage = new DamageComponent((int) (currentWeapon.baseDamage * boostScl));
						e.addComponent(damage);

						physicsSystem.registerCallback(projectile, e);

					}
				}

				// Push player backwards
				PhysicsComponent physics = getParent().getComponent(ComponentTypes.Physics);
				if (physics != null) {
					Vector2 vel = physics.getBody().getLinearVelocity();
					vel.add(heading.scl(-currentWeapon.knockback));
					physics.getBody().setLinearVelocity(vel);
				}
				cooldown = currentWeapon.cooldown;
				return true;
			}
		}
		return false;
	}

}
