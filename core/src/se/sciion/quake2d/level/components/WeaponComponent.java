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
import se.sciion.quake2d.level.system.SoundSystem;

/**
 * Keep track of cooldowns related to weapons.
 * 
 * @author sciion
 *
 */
public class WeaponComponent extends EntityComponent {

	private Level level;

	public WeaponComponent(Level level, PhysicsSystem physicsSystem, TextureRegion bulletTexture,
			TextureRegion muzzleTexture) {

		// Load the bullet texture here. A bit ugly, maybe do this in the level
		// instead?
		bulletTexture = new TextureRegion(new Texture(Gdx.files.internal("images/bullet.png")));

		this.level = level;
	}

	@Override
	public void render(RenderModel batch) {
		if (batch.debugging)
			return; // No debug render for this thing.
		SheetComponent spriteSheet = getParent().getComponent(ComponentTypes.Sheet);
		if (spriteSheet == null)
			return;

		InventoryComponent inventory = getParent().getComponent(ComponentTypes.Inventory);
		Array<Weapon> weapons = inventory.getItems(Weapon.class);

		if (weapons.size >= 1) {
			Weapon currentWeapon = weapons.first();
			
			currentWeapon.render(batch);
			if (currentWeapon.getTag().equals("shotgun"))
				spriteSheet.setCurrentRegion("gun");
			else if (currentWeapon.getTag().equals("rifle"))
				spriteSheet.setCurrentRegion("silencer");
			else if (currentWeapon.getTag().equals("sniper"))
				spriteSheet.setCurrentRegion("machine");
		} else
			spriteSheet.setCurrentRegion("stand");
	}

	@Override
	public void tick(float delta) {
		InventoryComponent inventory = getParent().getComponent(ComponentTypes.Inventory);
		if (inventory == null)
			return;

		Array<Weapon> weapons = inventory.getItems(Weapon.class);
		if (weapons.size >= 1) {
			weapons.first().tick(delta);
		}
	}

	@Override
	public ComponentTypes getType() {
		return ComponentTypes.Weapon;
	}

	// Used for now to fire weapon. Should perhaps be internal logic
	public boolean fire(Vector2 heading, Vector2 origin) {

		InventoryComponent inventory = getParent().getComponent(ComponentTypes.Inventory);
		if (inventory == null)
			return false;

		Array<Weapon> weapons = inventory.getItems(Weapon.class);
		if (weapons.size >= 1) {
			Weapon currentWeapon = weapons.first();

			if (currentWeapon.fire(origin, heading, parent)) {

				// Push player backwards
				PhysicsComponent physics = getParent().getComponent(ComponentTypes.Physics);
				if (physics != null) {
					Vector2 position = physics.getBody().getPosition();
					Vector2 vel = physics.getBody().getLinearVelocity();

					vel.add(heading.scl(-currentWeapon.knockback));
					physics.getBody().setLinearVelocity(vel);

					SoundSystem.getInstance().playSound(currentWeapon.getTag(), position, 1.0f);
				}

				return true;
			}
		}
		return false;
	}

}
