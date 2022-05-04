#![windows_subsystem = "windows"]

mod settings;
mod asset_loader;
mod entity;
mod camera;
mod loaders;
mod mapper;
mod toasts;
mod main_menu;
mod menu;
mod presence;

use bevy::prelude::*;
use bevy_inspector_egui::WorldInspectorPlugin;
use bevy_kira_audio::AudioPlugin;
use crate::asset_loader::AssetLoaderPlugin;
use crate::camera::CameraPlugin;
use crate::entity::EntityPlugin;
use crate::loaders::LoaderPlugin;
use crate::mapper::MapPlugin;
use crate::toasts::ToastsPlugin;
use crate::main_menu::MainMenuPlugin;
use crate::menu::InGameMenuPlugin;
use crate::presence::DiscordPlugin;

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
    app.add_plugin(WorldInspectorPlugin::new());

    app.add_plugin(AssetLoaderPlugin);
    app.add_plugin(MainMenuPlugin);
    app.add_plugin(InGameMenuPlugin);
    app.add_plugin(ToastsPlugin);
    app.add_plugin(LoaderPlugin);
    app.add_plugin(EntityPlugin);
    app.add_plugin(CameraPlugin);
    app.add_plugin(MapPlugin);
    // only use discordRPC if compiled with --release flag
    // unoptimized compiled version slows down the app
    if !cfg!(debug_assertions) {
        app.add_plugin(DiscordPlugin);
    }

    app.insert_resource(GlobalScale(Vec3::new(2.0, 2.0, 2.0)));

    app.run();
}

#[derive(Clone, Copy)]
pub struct GlobalScale(Vec3);

#[derive(Clone, Eq, PartialEq, Debug, Hash)]
pub enum AppState {
    // loading
    Preload,
    LoadingAssets,
    Loading(u8),
    MainMenu(MainMenus),
    // in-game
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