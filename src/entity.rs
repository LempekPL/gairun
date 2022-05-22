use bevy::prelude::*;
use bevy::render::texture::DEFAULT_IMAGE_HANDLE;
use bevy::sprite::collide_aabb::{collide, Collision};
use bevy_console::AddConsoleCommand;
use bevy_inspector_egui::{Inspectable, RegisterInspectable};
use crate::asset_loader::TextureAssets;
use crate::global::{AppState, Coords, GlobalScale, Hitbox};
use crate::global::InGameState::Playing;
use crate::global::MenuState::MainMenu;
use crate::settings::GameKeybinds;

pub struct EntityPlugin;

// components that any entity can have
#[derive(Component)]
pub struct GameEntity;

#[derive(Component)]
pub struct GravityEntity;

#[derive(Component, Inspectable)]
pub struct Motion {
    #[inspectable(min = 0.001, max = 2.0)]
    pub acc: f32,
    #[inspectable(min = 0.001, max = 2.0)]
    pub dcc: f32,
    #[inspectable(min = 0.001, max = 2000.0)]
    pub weight: f32,
    pub speed: Vec2,
}

// components specific to player
#[derive(Component)]
pub struct Player;

#[derive(Component, Inspectable, Default)]
pub struct Controllable {
    pub is_controllable: bool,
}

#[derive(Component, Inspectable, Default)]
pub struct Noclip {
    pub is_noclip: bool,
}

#[derive(Component, Inspectable, Default)]
pub struct Flying {
    pub is_flying: bool,
}

// components specific to enemies
#[derive(Component)]
pub struct AI;

impl Plugin for EntityPlugin {
    fn build(&self, app: &mut App) {
        app.add_system_set(SystemSet::on_enter(AppState::Game(Playing))
            .with_system(spawn_player)
        );
        app.add_system_set(SystemSet::on_update(AppState::Game(Playing))
            .with_system(controllable_user_keys
                .before(entity_motion)
            )
            .with_system(entity_motion)
            .with_system(entity_gravity_motion
                .before(entity_motion)
                .before(controllable_user_keys)
            )
            .with_system(entity_collision
                .after(entity_gravity_motion)
                .after(controllable_user_keys)
                .after(entity_motion)
            )
        );
        app.add_system_set(SystemSet::on_enter(AppState::Menu(MainMenu))
            .with_system(despawn_player)
        );
        app.register_inspectable::<Motion>();
        app.register_inspectable::<Controllable>();
        app.register_inspectable::<Noclip>();
        app.register_inspectable::<Flying>();
    }
}

fn spawn_player(
    mut commands: Commands,
    texture: Res<TextureAssets>,
    r_gs: Res<GlobalScale>,
) {
    commands.spawn_bundle(PlayerBundle {
        sprite: Sprite {
            custom_size: Some(Vec2::new(16.0, 32.0)),
            ..default()
        },
        texture: texture.gairun_walk_test.clone(),
        transform: Transform {
            scale: r_gs.0,
            ..default()
        },
        motion: Motion::new(0.1, 0.05, 1000.0),
        hitbox: Hitbox(Vec2::new(16., 32.)),
        ..default()
    })
        .insert(GameEntity)
        .insert(Player)
        .insert(GravityEntity)
        .insert(Name::new("Player"));
}

fn controllable_user_keys(
    mut q_motion: Query<(&mut Motion, &mut Sprite, &Flying, &Controllable)>,
    keys: Res<Input<KeyCode>>,
    game_keys: Res<GameKeybinds>,
    time: Res<Time>,
) {
    let delta = time.delta_seconds() * 100.0;
    for (mut motion, mut sprite, fly, cont) in q_motion.iter_mut() {
        if !cont.is_controllable { break; }
        let key_left = keys.pressed(game_keys.left);
        let key_right = keys.pressed(game_keys.right);
        // jump
        if keys.just_pressed(game_keys.up) && !fly.is_flying {
            motion.speed.y = 5.0;
        }
        // up down
        if fly.is_flying {
            let key_up = keys.pressed(game_keys.up);
            let key_down = keys.pressed(game_keys.down);
            if key_down {
                motion.speed.y -= motion.acc * delta;
            }
            if key_up {
                motion.speed.y += motion.acc * delta;
            }
            if (key_down && key_up) || (!key_down && !key_up) {
                motion.speed.y -= motion.speed.y * motion.dcc * delta.clamp(0.0, 0.9);
            }
            motion.speed.y = motion.speed.y.clamp(-5.0, 5.0);
        }
        // left right
        if key_left {
            motion.speed.x -= motion.acc * delta;
            sprite.flip_x = true;
        }
        if key_right {
            motion.speed.x += motion.acc * delta;
            sprite.flip_x = false;
        }
        if (key_left && key_right) || (!key_left && !key_right) {
            motion.speed.x -= motion.speed.x * motion.dcc * delta.clamp(0.0, 0.9);
        }
        motion.speed.x = motion.speed.x.clamp(-5.0, 5.0);
    }
}

fn entity_motion(
    mut p_movement: Query<(&mut Transform, &Motion), With<GameEntity>>,
    time: Res<Time>,
) {
    let delta = time.delta_seconds() * 100.0;
    for (mut movement, motion) in p_movement.iter_mut() {
        movement.translation.x += motion.speed.x * delta;
        movement.translation.y += motion.speed.y * delta;
    }
}

fn entity_gravity_motion(
    mut q_motion: Query<(&mut Motion, Option<&Flying>), With<GravityEntity>>,
    time: Res<Time>,
) {
    let delta = time.delta_seconds();
    for (mut motion, flying) in q_motion.iter_mut() {
        if let Some(fly) = flying {
            if fly.is_flying { return; }
        }
        motion.speed.y -= 9.81 * motion.weight / 1000.0 * delta;
    }
}

fn entity_collision(
    q_block: Query<(&Coords, &Hitbox)>,
    mut q_entity: Query<(&mut Transform, &mut Motion, &Hitbox, &Noclip)>,
    r_gs: Res<GlobalScale>,
) {
    if let Ok((mut transform, mut motion, e_hitbox, noclip)) = q_entity.get_single_mut() {
        if noclip.is_noclip { return; }
        for (b_coords, b_hitbox) in q_block.iter() {
            // change values to match the scale
            let a_size = Vec2::new(e_hitbox.0.x * r_gs.0.x, e_hitbox.0.y * r_gs.0.y);
            let b_pos = Vec3::new(b_coords.0.x * 16. * r_gs.0.x, b_coords.0.y * 16. * r_gs.0.y, 1.);
            let b_size = Vec2::new(b_hitbox.0.x * r_gs.0.x, b_hitbox.0.y * r_gs.0.y);
            // check for collisions
            if let Some(colliding) = collide(transform.translation, a_size, b_pos, b_size) {
                match colliding {
                    Collision::Left => {
                        motion.speed.x = 0.;
                        transform.translation.x = (b_coords.0.x * 16.) * r_gs.0.x - (a_size.x + b_size.x) / 2.;
                    }
                    Collision::Right => {
                        motion.speed.x = 0.;
                        transform.translation.x = (b_coords.0.x * 16.) * r_gs.0.x + (a_size.x + b_size.x) / 2.;
                    }
                    Collision::Top => {
                        motion.speed.y = 0.;
                        transform.translation.y = (b_coords.0.y * 16.) * r_gs.0.y + (a_size.y + b_size.y) / 2.;
                    }
                    Collision::Bottom => {
                        motion.speed.y = 0.;
                        transform.translation.y = (b_coords.0.y * 16.) * r_gs.0.y - (a_size.y + b_size.y) / 2.;
                    }
                    _ => {}
                }
            }
        }
    }
}

fn despawn_player(
    mut commands: Commands,
    player: Query<Entity, With<Player>>,
) {
    let player = player.get_single();
    if let Ok(player) = player {
        commands.
            entity(player)
            .despawn_recursive();
    }
}

#[derive(Bundle)]
pub struct PlayerBundle {
    pub sprite: Sprite,
    pub transform: Transform,
    pub global_transform: GlobalTransform,
    pub texture: Handle<Image>,
    pub hitbox: Hitbox,
    pub visibility: Visibility,
    pub flying: Flying,
    pub noclip: Noclip,
    pub controllable: Controllable,
    pub motion: Motion,
}

impl Default for PlayerBundle {
    fn default() -> Self {
        Self {
            sprite: Default::default(),
            transform: Default::default(),
            global_transform: Default::default(),
            texture: DEFAULT_IMAGE_HANDLE.typed(),
            hitbox: Default::default(),
            visibility: Default::default(),
            flying: Default::default(),
            noclip: Default::default(),
            controllable: Default::default(),
            motion: Default::default(),
        }
    }
}

impl Motion {
    fn new(acc: f32, dcc: f32, weight: f32) -> Self {
        Self {
            acc,
            dcc,
            weight,
            speed: Vec2::new(0.0, 0.0),
        }
    }
}

impl Default for Motion {
    fn default() -> Self {
        Self {
            acc: 1.0,
            dcc: 1.0,
            weight: 1.0,
            speed: Vec2::new(0.0, 0.0),
        }
    }
}