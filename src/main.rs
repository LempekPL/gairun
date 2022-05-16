#![windows_subsystem = "windows"]

mod global;
mod settings;
mod asset_loader;
mod entity;
mod camera;
mod loaders;
mod mapper;
mod toasts;
mod main_menu;
mod menu;

use bevy::prelude::*;
use bevy_inspector_egui::WorldInspectorPlugin;
use bevy_kira_audio::AudioPlugin;
use global::GlobalScale;
use crate::asset_loader::AssetLoaderPlugin;
use crate::camera::CameraPlugin;
use crate::entity::EntityPlugin;
use crate::loaders::LoaderPlugin;
use crate::mapper::MapPlugin;
use crate::toasts::ToastsPlugin;
use crate::main_menu::MainMenuPlugin;
use crate::menu::InGameMenuPlugin;

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

    app.add_plugin(AssetLoaderPlugin);
    app.add_plugin(MainMenuPlugin);
    app.add_plugin(InGameMenuPlugin);
    app.add_plugin(ToastsPlugin);
    app.add_plugin(LoaderPlugin);
    app.add_plugin(EntityPlugin);
    app.add_plugin(CameraPlugin);
    app.add_plugin(MapPlugin);

    app.insert_resource(GlobalScale(Vec3::new(2.0, 2.0, 2.0)));

    app.run();
}