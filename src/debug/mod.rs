mod debug_lines;
mod console;

//
// use bevy::ecs::schedule::ShouldRun;
use bevy::prelude::*;
use self::console::DebugConsolePlugin;
// use bevy_inspector_egui::WorldInspectorParams;
// use crate::debug::debug_lines::{debug_lines_blocks};
//
#[derive(Debug, PartialEq, Eq, Resource)]
pub struct IsDebug(pub bool);
//
// fn if_debug_on(
//     debug: Res<IsDebug>
// ) -> ShouldRun {
//     if debug.0 {
//         ShouldRun::Yes
//     } else {
//         ShouldRun::No
//     }
// }
//
pub struct DebugPlugin;
//
impl Plugin for DebugPlugin {
    fn build(&self, app: &mut App) {
        app.add_plugin(DebugConsolePlugin);
        // app.add_system(enable_world_inspector);
        // app.add_system_set(SystemSet::new().with_run_criteria(if_debug_on)
        //     .with_system(debug_lines_blocks)
        // );
    }
}
//
// fn enable_world_inspector(
//     input: Res<Input<KeyCode>>,
//     // game_keys: Res<GameKeybinds>,
//     mut world_inspector_params: ResMut<WorldInspectorParams>
// ) {
//     if input.just_pressed(KeyCode::F1) {
//         world_inspector_params.enabled = !world_inspector_params.enabled;
//     }
// }