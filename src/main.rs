#![windows_subsystem = "windows"]

mod asset_loader;
mod camera;
mod entity;
mod global;
mod loaders;
mod mapper;
mod settings;
mod ui;

use bevy::prelude::*;
use bevy_inspector_egui::WorldInspectorPlugin;
use bevy_kira_audio::AudioPlugin;
use global::GlobalScale;
use crate::asset_loader::AssetLoaderPlugin;
use crate::camera::CameraPlugin;
use crate::entity::EntityPlugin;
use crate::global::{GlobalPlugin, GlobalScale};
use crate::loaders::LoaderPlugin;
use crate::mapper::MapPlugin;
use crate::ui::UiPlugin;

fn main() {
    let mut app = App::new();
    app.insert_resource(ClearColor(Color::BLACK));
    app.insert_resource(WindowDescriptor {
        title: "gairun".to_string(),
        resizable: false,
        width: 1920.0,
        height: 1080.0,
        ..Default::default()
    });
    app.add_plugins(DefaultPlugins);
    app.add_plugin(AudioPlugin);
    app.add_plugin(WorldInspectorPlugin::new());

    app.add_plugin(GlobalPlugin);
    app.add_plugin(AssetLoaderPlugin);
    app.add_plugin(LoaderPlugin);
    app.add_plugin(EntityPlugin);
    app.add_plugin(CameraPlugin);
    app.add_plugin(MapPlugin);
    app.add_plugin(UiPlugin);

    app.insert_resource(GlobalScale(Vec3::new(2.0, 2.0, 2.0)));

    app.run();
}