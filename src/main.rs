mod camera;
mod debug;
mod entity;
mod global;
mod loaders;
mod mapper;
mod settings;
mod ui;

use bevy::prelude::*;
use bevy_inspector_egui::quick::WorldInspectorPlugin;
use bevy_kira_audio::AudioPlugin;
use bevy_prototype_debug_lines::DebugLinesPlugin;
use crate::camera::CameraPlugin;
use crate::debug::{DebugPlugin, IsDebug};
use crate::entity::{EntityPlugin, Flying, Noclip};
use crate::global::GlobalPlugin;
use crate::loaders::LoaderPlugin;
use crate::mapper::{LoadMapEvent, MapPlugin};
use crate::ui::UiPlugin;

fn main() {
    let mut app = App::new();
    app.insert_resource(ClearColor(Color::BLACK));
    app.add_plugins(DefaultPlugins.set(WindowPlugin {
        primary_window: Some(Window {
            position: WindowPosition::Centered(MonitorSelection::Primary),
            title: "gairun".to_string(),
            decorations: true,
            resizable: false,
            focused: true,
            ..Default::default()
        }),
        ..Default::default()
    }).set(ImagePlugin::default_nearest()));
    app.add_plugin(AudioPlugin);
    app.add_plugin(DebugLinesPlugin::default());
    app.add_plugin(WorldInspectorPlugin::default());

    app.add_plugin(GlobalPlugin);
    app.add_plugin(LoaderPlugin);
    app.add_plugin(EntityPlugin);
    app.add_plugin(CameraPlugin);
    app.add_plugin(MapPlugin);
    app.add_plugin(UiPlugin);
    app.add_plugin(DebugPlugin);

    app.run();
}