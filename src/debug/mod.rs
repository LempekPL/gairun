mod debug;

use bevy::ecs::schedule::ShouldRun;
use bevy::prelude::*;
use bevy_console::{AddConsoleCommand, ConsolePlugin};
use bevy_inspector_egui::WorldInspectorParams;
use crate::debug::debug::debug_lines;

#[derive(Debug, PartialEq, Eq)]
pub struct IsDebug(pub bool);

fn if_debug_on(
    debug: Res<IsDebug>
) -> ShouldRun {
    if debug.0 {
        ShouldRun::Yes
    } else {
        ShouldRun::No
    }
}

pub struct DebugPlugin;

impl Plugin for DebugPlugin {
    fn build(&self, app: &mut App) {
        app.add_system(enable_world_inspector);
        app.add_system_set(SystemSet::new().with_run_criteria(if_debug_on)
            .with_system(debug_lines)
        );
    }
}

fn enable_world_inspector(
    input: Res<Input<KeyCode>>,
    // game_keys: Res<GameKeybinds>,
    mut world_inspector_params: ResMut<WorldInspectorParams>
) {
    if input.just_pressed(KeyCode::F1) {
        world_inspector_params.enabled = !world_inspector_params.enabled;
    }
}