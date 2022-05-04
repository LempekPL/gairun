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

use bevy::prelude::*;
use bevy_discord_presence::config::{RPCConfig, RPCPlugin};
use bevy_discord_presence::state::ActivityState;
use bevy_inspector_egui::WorldInspectorPlugin;
use bevy_kira_audio::AudioPlugin;
use discord_presence::models::ActivityAssets;
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
    // only use discordRPC if compiled with --release flag
    // unoptimized compiled version slows down the app
    if !cfg!(debug_assertions) {
        app.add_plugin(RPCPlugin(
            RPCConfig {
                app_id: 971525507541790720,
                show_time: true,
            }
        ));
        app.add_startup_system(update_presence);
    }

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

fn update_presence(mut state: ResMut<ActivityState>) {
    // add discord presence
    state.details = Some("Playing".to_string());
    state.assets = Some(ActivityAssets {
        large_image: Some("icon".to_string()),
        large_text: Some("gairun".to_string()),
        small_image: None,
        small_text: None,
    });
}