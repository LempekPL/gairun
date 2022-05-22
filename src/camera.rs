use bevy::prelude::*;
use bevy::render::camera::Camera2d;
use crate::global::AppState;
use crate::entity::Player;
use crate::global::InGameState::Playing;

pub(crate) struct CameraPlugin;

const BOX_SIZE: f32 = 64.0;
const CAMERA_SPEED: f32 = 0.064;

impl Plugin for CameraPlugin {
    fn build(&self, app: &mut App) {
        app
            .add_system_set(SystemSet::on_update(AppState::Game(Playing))
                .with_system(camera_follow_player)
            );
    }
}

#[allow(clippy::type_complexity)]
fn camera_follow_player(
    mut q_camera_player: ParamSet<(Query<&mut Transform, With<Camera2d>>, Query<&Transform, With<Player>>)>
) {
    let player = q_camera_player.p1();
    let player = player.get_single();
    if let Ok(player) = player {
        let (player_x, player_y) = (player.translation.x, player.translation.y);
        let mut q_camera = q_camera_player.p0();
        let mut camera = q_camera.single_mut();

        camera.translation.x += move_camera(camera.translation.x, player_x);
        camera.translation.y += move_camera(camera.translation.y, player_y);
    } else {
        let mut q_camera = q_camera_player.p0();
        let mut camera = q_camera.single_mut();

        camera.translation.x += move_camera(camera.translation.x, 0.);
        camera.translation.y += move_camera(camera.translation.y, 0.);
    }
}

// helper function
fn move_camera(camera: f32, player: f32) -> f32 {
    if camera - BOX_SIZE > player {
        return (player - camera + BOX_SIZE) * CAMERA_SPEED;
    }
    if camera + BOX_SIZE < player {
        return (player - camera - BOX_SIZE) * CAMERA_SPEED;
    }
    0.
}