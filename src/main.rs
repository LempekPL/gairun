#![windows_subsystem = "windows"]

mod main_menu;
mod asset_loader;
mod toasts;
mod loaders;
mod entity;
mod camera;
mod menu;
mod map_generation;
mod settings;

use bevy::prelude::*;
use bevy::window::WindowMode;
use bevy_inspector_egui::WorldInspectorPlugin;
use bevy_kira_audio::AudioPlugin;
use serde::{Serialize, Deserialize};
use crate::asset_loader::AssetLoaderPlugin;
use crate::camera::CameraPlugin;
use crate::entity::EntityPlugin;
use crate::loaders::LoaderPlugin;
use crate::main_menu::MainMenuPlugin;
use crate::menu::InGameMenuPlugin;
use crate::toasts::ToastsPlugin;

fn main() {
    let mut app = App::new();
    app.insert_resource(ClearColor(Color::BLACK));
    app.insert_resource(WindowDescriptor {
        title: "Gairun".to_string(),
        resizable: false,
        width: 1920.0,
        height: 1080.0,
        ..Default::default()
    });
    app.add_plugins(DefaultPlugins);
    app.add_plugin(AudioPlugin);
    app.add_plugin(AssetLoaderPlugin);
    app.add_plugin(MainMenuPlugin);
    app.add_plugin(InGameMenuPlugin);
    app.add_plugin(ToastsPlugin);
    app.add_plugin(LoaderPlugin);
    app.add_plugin(EntityPlugin);
    app.add_plugin(CameraPlugin);

    app.add_plugin(WorldInspectorPlugin::new());
    app.run();
}

#[derive(Clone, Eq, PartialEq, Debug, Hash)]
pub enum AppState {
    // loading
    Preload,
    LoadingAssets,
    Loading(u8),
    MainMenu(MainMenus),
    // in-game
    LoadingMap,
    Game(InGameState),
}

#[derive(Clone, Eq, PartialEq, Debug, Hash)]
pub enum MainMenus {
    Main,
    Settings(u8),
    Credit,
}

#[derive(Clone, Eq, PartialEq, Debug, Hash)]
pub enum InGameState {
    Playing,
    Paused,
}