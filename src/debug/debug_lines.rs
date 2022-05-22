use bevy::prelude::*;
use bevy_prototype_debug_lines::DebugLines;
use crate::global::{Coords, GlobalScale, Hitbox};

pub fn debug_lines_blocks(
    mut lines: ResMut<DebugLines>,
    q_debuggable: Query<(&Coords, &Hitbox)>,
    r_gs: Res<GlobalScale>,
) {
    // offset
    let c = |coord,hitbox| { coord * 16. - 8. + hitbox };
    for (coords, hitbox) in q_debuggable.iter() {
        // top
        let start1 = Vec3::new(
            c(coords.0.x, 0.),
            c(coords.0.y, 0.),
            6.0);
        let end1 = Vec3::new(
            c(coords.0.x,hitbox.0.x),
            c(coords.0.y, 0.),
            6.0);
        lines.line_colored(start1 * r_gs.0, end1 * r_gs.0, 0.0, Color::RED);
        // left
        let start2 = Vec3::new(
            c(coords.0.x, 0.),
            c(coords.0.y, 0.),
            6.0);
        let end2 = Vec3::new(
            c(coords.0.x,0.),
            c(coords.0.y, hitbox.0.y),
            6.0);
        lines.line_colored(start2 * r_gs.0, end2 * r_gs.0, 0.0, Color::RED);
        // bottom
        let start3 = Vec3::new(
            c(coords.0.x, hitbox.0.x),
            c(coords.0.y, 0.),
            6.0);
        let end3 = Vec3::new(
            c(coords.0.x,hitbox.0.x),
            c(coords.0.y, hitbox.0.y),
            6.0);
        lines.line_colored(start3 * r_gs.0, end3 * r_gs.0, 0.0, Color::RED);
        // right
        let start4 = Vec3::new(
            c(coords.0.x, hitbox.0.x),
            c(coords.0.y, hitbox.0.y),
            6.0);
        let end4 = Vec3::new(
            c(coords.0.x,0.),
            c(coords.0.y, hitbox.0.y),
            6.0);
        lines.line_colored(start4 * r_gs.0, end4 * r_gs.0, 0.0, Color::RED);
    }
}