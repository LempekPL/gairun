use bevy::prelude::*;
use bevy_inspector_egui::egui::Shape::Vec;
use crate::AppState;
use crate::InGameState::Playing;

pub struct EntityPlugin;

// components that any entity can have
#[derive(Component)]
pub struct GameEntity;

#[derive(Component)]
pub struct GravityEntity;

#[derive(Component)]
pub struct Motion {
    acc: f32,
    dcc: f32,
    weight: f32,
    speed: Vec2,
}

#[derive(Component)]
pub struct Health(f32);

#[derive(Component)]
pub struct Visible;

#[derive(Component)]
pub struct Noclip;

// components specific to player
#[derive(Component)]
pub struct Player;

#[derive(Component)]
pub struct Fly;

#[derive(Component)]
pub struct Controllable;

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
            .add_system_set(SystemSet::on_exit(AppState::Game(Playing))
                .with_system(despawn_player)
            );
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
        .insert(Motion::new(0.5, 0.05, 1000.0))
        .insert(Controllable);
}

fn controllable_user_keys(
    mut q_motion: Query<&mut Motion, With<Controllable>>,
    keys: Res<Input<KeyCode>>,
    time: Res<Time>,
) {
    let delta = time.delta_seconds()*100.0;
    for mut motion in q_motion.iter_mut() {
        let key_a = keys.pressed(KeyCode::A);
        let key_d = keys.pressed(KeyCode::D);
        if keys.just_pressed(KeyCode::W) {
            motion.speed.y = 5.0;
        }
        if key_a {
            motion.speed.x -= motion.acc * delta;
        }
        if key_d {
            motion.speed.x += motion.acc * delta;
        }
        if (key_a && key_d) || (!key_a && !key_d) {
            motion.speed.x -= (motion.speed.x * motion.dcc * delta.clamp(0.0, 0.9));
        }
        motion.speed.x = motion.speed.x.clamp(-5.0, 5.0);
    }
}

fn entity_motion(
    mut p_movement: Query<(&mut Transform, &Motion), With<GameEntity>>,
    time: Res<Time>,
) {
    let delta = time.delta_seconds()*100.0;
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
    let player = player.single();
    commands.
        entity(player)
        .despawn_recursive();
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