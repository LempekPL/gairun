use bevy::prelude::*;
use crate::{AppState, GameKeys};
use crate::InGameState::Playing;
use crate::MainMenus::Main;

pub struct EntityPlugin;

// components that any entity can have
#[derive(Component)]
pub struct GameEntity;

#[derive(Component)]
pub struct GravityEntity;

#[derive(Reflect, Component)]
#[reflect(Component)]
pub struct Motion {
    pub acc: f32,
    pub dcc: f32,
    pub weight: f32,
    pub speed: Vec2,
}

// components specific to player
#[derive(Component)]
pub struct Player;

#[derive(Component)]
pub struct Controllable;

#[derive(Reflect, Component)]
#[reflect(Component)]
pub struct EntitySettings {
    pub visible: bool,
    pub noclip: bool,
    pub flying: bool,
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
            .add_system_set(SystemSet::on_enter(AppState::MainMenu(Main))
                .with_system(despawn_player)
            );
        app
            .add_system(disable_visibility_on_change);
        app
            .register_type::<Motion>()
            .register_type::<EntitySettings>();
    }
}

fn spawn_player(
    // currentMap: Res<CurrentMap>
    mut commands: Commands,
    // texture: Res<Image>
) {
    commands
        .spawn_bundle(SpriteBundle {
            sprite: Sprite {
                color: Color::RED,
                custom_size: Some(Vec2::new(32.0, 64.0)),
                ..Default::default()
            },
            ..Default::default()
        })
        .insert(GameEntity)
        .insert(Player)
        .insert(GravityEntity)
        .insert(Motion::new(0.1, 0.05, 1000.0))
        .insert(Controllable)
        .insert(EntitySettings::default());
}

fn controllable_user_keys(
    mut q_motion: Query<(&mut Motion, &EntitySettings), With<Controllable>>,
    keys: Res<Input<KeyCode>>,
    game_keys: Res<GameKeys>,
    time: Res<Time>,
) {
    let delta = time.delta_seconds() * 100.0;
    for (mut motion, e_settings) in q_motion.iter_mut() {
        let key_left = keys.pressed(game_keys.left);
        let key_right = keys.pressed(game_keys.right);
        // jump
        if keys.just_pressed(game_keys.up) && !e_settings.flying {
            motion.speed.y = 5.0;
        }
        // up down
        if e_settings.flying {
            let key_up = keys.pressed(game_keys.left);
            let key_down = keys.pressed(game_keys.right);
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
    mut q_motion: Query<(&Transform, &mut Motion), With<GravityEntity>>,
    time: Res<Time>,
) {
    let delta = time.delta_seconds();
    for (tr, mut motion) in q_motion.iter_mut() {
        if tr.translation.y > 0.0 {
            motion.speed.y -= 9.81 * motion.weight / 1000.0 * delta;
        } else if motion.speed.y < 0.0 {
            motion.speed.y = 0.0;
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

fn disable_visibility_on_change(
    mut q_entity: Query<(&mut Visibility, &EntitySettings), Changed<EntitySettings>>,
) {
    for (mut vis, e_settings) in q_entity.iter_mut() {
        vis.is_visible = e_settings.visible
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
            weight: 0.0,
            speed: Vec2::new(0.0, 0.0),
        }
    }
}

impl Default for EntitySettings {
    fn default() -> Self {
        Self {
            visible: true,
            noclip: false,
            flying: false,
        }
    }
}