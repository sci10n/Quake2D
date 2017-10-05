package se.sciion.quake2d.level.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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
	private PhysicsSystem physicsSystem;
	private Level level;

	public WeaponComponent(Level level, PhysicsSystem physicsSystem) {
		cooldown = 0;
		this.physicsSystem = physicsSystem;
		this.level = level;
	}

	@Override
	public void render(RenderModel batch) {

		InventoryComponent inventory = getParent().getComponent(ComponentTypes.Inventory);
		if (inventory == null)
			return;

		Weapon currentWeapon = null;
		Array<Weapon> weapons = inventory.getItems(Weapon.class);

		if (weapons.size >= 1) {
			currentWeapon = weapons.first();

			PhysicsComponent physics = getParent().getComponent(ComponentTypes.Physics);
			if (physics != null) {
				Vector2 origin = physics.getBody().getPosition();

				float angle = physics.getBody().getAngle();
				float spread = (MathUtils.degreesToRadians * currentWeapon.spread / 2.0f);
				Vector2 heading1 = new Vector2(MathUtils.cos(angle - spread), MathUtils.sin(angle - spread));
				Vector2 heading2 = new Vector2(MathUtils.cos(angle + spread), MathUtils.sin(angle + spread));
				batch.primitiveRenderer.setColor(Color.GRAY);

				batch.primitiveRenderer.line(origin, origin.cpy().add(heading1.scl(2)));
				batch.primitiveRenderer.line(origin, origin.cpy().add(heading2.scl(2)));

				if (cooldown > 0) {
					batch.primitiveRenderer.setColor(Color.BLUE);
					float full = 360.0f / currentWeapon.cooldown;
					batch.primitiveRenderer.arc(origin.x, origin.y, 1.0f, 0, full * cooldown, 100);
				}
			}
		}
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
						float x = origin.x + bulletHeading.x;
						float y = origin.y + bulletHeading.y;
						Entity e = level.createEntity();
						CircleShape circle = new CircleShape();
						circle.setRadius(0.2f);
						PhysicsComponent bulletPhysics = physicsSystem.createComponent(x, y, BodyType.DynamicBody,
								circle);
						e.addComponent(bulletPhysics);

						ProjectileComponent projectile = new ProjectileComponent(
								bulletHeading.cpy().scl(currentWeapon.speed));
						e.addComponent(projectile);

						DamageComponent damage = new DamageComponent(1);
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
