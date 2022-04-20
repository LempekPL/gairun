use bevy::prelude::*;
use crate::{AppState, Camera2D};
use crate::entity::Player;
use crate::InGameState::Playing;

pub(crate) struct CameraPlugin;

impl Plugin for CameraPlugin {
    fn build(&self, app: &mut App) {
        app
            .add_system_set(SystemSet::on_update(AppState::Game(Playing))
                .with_system(camera_follow_player)
            );
    }
}

fn camera_follow_player(
    mut p_cam_player: ParamSet<(Query<&mut Transform, With<Camera2d>>, Query<&Transform, With<Player>>)>
) {
    let player = p_cam_player.p1();
    let player = player.single();
    let x = player.translation.x;
    let y = player.translation.y;
    let mut q_cam = p_cam_player.p0();
    let mut cam = q_cam.single_mut();
    cam.translation.x = x;
    cam.translation.y = y;
}