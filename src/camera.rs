use bevy::prelude::*;
use bevy::render::camera::Camera2d;
use crate::AppState;
use crate::entity::Player;
use crate::InGameState::Playing;

pub(crate) struct CameraPlugin;

const BOX_SIZE: f32 = 30.0;

impl Plugin for CameraPlugin {
    fn build(&self, app: &mut App) {
        app
            .add_system_set(SystemSet::on_update(AppState::Game(Playing))
                .with_system(camera_follow_player)
            );
    }
}

#[warn(clippy::type_complexity)]
fn camera_follow_player(
    mut p_cam_player: ParamSet<(Query<&mut Transform, With<Camera2d>>, Query<&Transform, With<Player>>)>
) {
    let player = p_cam_player.p1();
    let player = player.single();
    let (p_x, p_y) = (player.translation.x, player.translation.y);
    let mut q_cam = p_cam_player.p0();
    let mut cam = q_cam.single_mut();
    let (mut c_x, mut c_y) = (cam.translation.x, cam.translation.y);

    if c_x - BOX_SIZE > p_x {
        c_x += (p_x - c_x + BOX_SIZE) / BOX_SIZE;
    } else if c_x + BOX_SIZE < p_x {
        c_x += (p_x - c_x - BOX_SIZE) / BOX_SIZE;
    }
    if c_y - BOX_SIZE > p_y {
        c_y += (p_y - c_y + BOX_SIZE) / BOX_SIZE;
    } else if c_y + BOX_SIZE < p_y {
        c_y += (p_y - c_y - BOX_SIZE) / BOX_SIZE;
    }
    cam.translation.x = c_x;
    cam.translation.y = c_y;
}