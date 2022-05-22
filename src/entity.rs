use bevy::prelude::*;
use bevy::render::texture::DEFAULT_IMAGE_HANDLE;
use bevy::sprite::collide_aabb::{collide, Collision};
use bevy_prototype_debug_lines::DebugLines;
use crate::global::{AppState, Coords, GlobalScale, Hitbox};
use crate::global::InGameState::Playing;
use crate::global::MenuState::Main;
use crate::settings::GameKeybinds;
// use bevy_render::texture::DEFAULT_IMAGE_HANDLE;

pub struct EntityPlugin;

// components that any entity can have
#[derive(Component)]
pub struct GameEntity;

#[derive(Component)]
pub struct GravityEntity;

#[derive(Component)]
pub struct Motion {
    pub acc: f32,
    pub dcc: f32,
    pub weight: f32,
    pub speed: Vec2,
}

// components specific to player
#[derive(Component)]
pub struct Player;

#[derive(Component, Default)]
pub struct Controllable {
    pub is_controllable: bool,
}

#[derive(Component, Default)]
pub struct Noclip {
    pub is_noclip: bool,
}

#[derive(Component, Default)]
pub struct Flying {
    pub is_flying: bool,
}

// components specific to enemies
#[derive(Component)]
pub struct AI;

impl Plugin for EntityPlugin {
    fn build(&self, app: &mut App) {
        app
            .add_system_set(SystemSet::on_enter(AppState::Game(Playing))
                .with_system(spawn_player)
            )
            .add_system_set(SystemSet::on_update(AppState::Game(Playing))
                .with_system(controllable_user_keys)
                .with_system(entity_motion)
                .with_system(entity_gravity_motion)
            )
            .add_system_set(SystemSet::on_enter(AppState::Menu(Main))
                .with_system(despawn_player)
            );
    }
}

fn spawn_player(
    // currentMap: Res<CurrentMap>
    mut commands: Commands,
    // texture: Res<Image>
    r_gs: Res<GlobalScale>,
) {
    commands.spawn_bundle(PlayerBundle {
        sprite: Sprite {
            color: Color::RED,
            custom_size: Some(Vec2::new(16.0, 32.0)),
            ..default()
        },
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
        .insert(GravityEntity);
}

fn controllable_user_keys(
    mut q_motion: Query<(&mut Motion, &Flying, &Controllable)>,
    keys: Res<Input<KeyCode>>,
    game_keys: Res<GameKeybinds>,
    time: Res<Time>,
) {
    let delta = time.delta_seconds() * 100.0;
    for (mut motion, fly, cont) in q_motion.iter_mut() {
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
        }
        if key_right {
            motion.speed.x += motion.acc * delta;
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