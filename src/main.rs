#![windows_subsystem = "windows"]
mod main_menu;
mod asset_loader;
mod toasts;
mod loaders;

use bevy::prelude::*;
use bevy_kira_audio::AudioPlugin;
use serde::{Serialize, Deserialize};
use crate::asset_loader::AssetLoaderPlugin;
use crate::main_menu::MainMenuPlugin;
use crate::toasts::ToastsPlugin;
use crate::loaders::LoaderPlugin;

const DEFAULT_WIDTH: f32 = 1280.0;
const DEFAULT_HEIGHT: f32 = 720.0;

#[derive(Clone, Eq, PartialEq, Debug, Hash)]
enum AppState {
    // loading
    Preload,
    LoadingAssets,
    Loading(u8),
    // main menu
    MainMenu,
    Settings,
    // in-game
    LoadingMap,
    Game,
}

#[derive(Deserialize, Serialize, Copy, Clone)]
struct GameSettings {
    volume: f32,
    resolution: (f32, f32),
}

impl Default for GameSettings {
    fn default() -> Self {
        Self {
            volume: 1.0,
            resolution: (DEFAULT_WIDTH, DEFAULT_HEIGHT),
        }
    }
}

// global components
#[derive(Component)]
struct CameraUI;

#[derive(Component)]
struct Camera2D;

fn main() {
    let mut app = App::new();
    app.insert_resource(ClearColor(Color::BLACK));
    app.insert_resource(WindowDescriptor {
        title: "Gairun".to_string(),
        width: DEFAULT_WIDTH,
        height: DEFAULT_HEIGHT,
        position: Some(Vec2::new(0.0, 0.0)),
        resizable: false,
        ..Default::default()
    });
    app.add_plugins(DefaultPlugins);
    app.add_plugin(AudioPlugin);
    app.add_plugin(AssetLoaderPlugin);
    app.add_plugin(MainMenuPlugin);
    app.add_plugin(ToastsPlugin);
    app.add_plugin(LoaderPlugin);
    app.run();
}