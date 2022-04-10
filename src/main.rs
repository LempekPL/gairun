mod main_menu;
mod asset_loader;

use bevy::prelude::*;
use bevy_kira_audio::{Audio, AudioPlugin};
use serde::{Serialize, Deserialize};
use std::fs::{self};
use ron::ser::{PrettyConfig, to_string_pretty};
use crate::asset_loader::{AssetLoaderPlugin, SoundAssets};
use crate::main_menu::MainMenuPlugin;

const DEFAULT_WIDTH: f32 = 1280.0 / 2.0;
const DEFAULT_HEIGHT: f32 = 960.0 / 2.0;

#[derive(Clone, Eq, PartialEq, Debug, Hash)]
enum AppState {
    // loading
    Preload,
    LoadingAssets,
    Loading,
    // main menu
    MainMenu,
    Settings,
    // in-game
    LoadingMap,
    Game,
}

#[derive(Deserialize, Serialize)]
struct GameSettings {
    volume: f32,
}

#[derive(Component)]
struct Camera2D;

#[derive(Component)]
struct CameraUI;

#[derive(Component)]
struct LoadingText;

fn main() {
    let mut app = App::new();
    app.insert_resource(ClearColor(Color::BLACK.into()));
    app.insert_resource(WindowDescriptor {
        title: "Gairun".to_string(),
        width: DEFAULT_WIDTH,
        height: DEFAULT_HEIGHT,
        resizable: false,
        ..Default::default()
    });
    app.add_startup_system(preload);
    // this will tell the asset loader to load assets after loading assets it will change state loding to initiate setup
    app.add_state(AppState::Preload);
    app.add_plugins(DefaultPlugins);
    app.add_plugin(AudioPlugin);
    // plugin that loads all needed assets
    app.add_plugin(AssetLoaderPlugin);
    // main menu systems
    app.add_plugin(MainMenuPlugin);
    // initiating setup
    app.add_system_set(SystemSet::on_enter(AppState::Loading).with_system(setup));

    app.run();
}

fn preload(
    mut commands: Commands,
    mut app_state: ResMut<State<AppState>>,
    asset_server: Res<AssetServer>,
) {
    // camera ui
    commands.spawn_bundle(UiCameraBundle::default()).insert(CameraUI);
    // loading text spawn
    commands.spawn_bundle(NodeBundle {
        style: Style {
            size: Size::new(Val::Percent(100.0), Val::Percent(100.0)),
            position_type: PositionType::Absolute,
            justify_content: JustifyContent::Center,
            align_items: AlignItems::FlexEnd,
            ..Default::default()
        },
        color: UiColor(Color::BLACK),
        ..Default::default()
    }).with_children(|parent| {
        parent.spawn_bundle(TextBundle {
            style: Style {
                align_self: AlignSelf::Center,
                ..Default::default()
            },
            text: Text::with_section(
                "LOADING",
                TextStyle {
                    font: asset_server.load("fonts/open_sans/OpenSans-Bold.ttf"),
                    font_size: 80.0,
                    color: Color::WHITE,
                },
                Default::default(),
            ),
            ..Default::default()
        });
    }).insert(LoadingText);
    // load game settings
    commands.insert_resource(load_settings());
    // start loading assets
    app_state.set(AppState::LoadingAssets).unwrap();
}

// HELPER function, NOT system
fn load_settings() -> GameSettings {
    if let Ok(saved_settings) = fs::read_to_string("./assets/settings/config.ron") {
        let settings: Result<GameSettings, _> = ron::from_str(&saved_settings);
        if settings.is_ok() {
            settings.unwrap()
        } else {
            create_settings()
        }
    } else {
        create_settings()
    }
}

// HELPER function, NOT system
fn create_settings() -> GameSettings {
    let settings = GameSettings {
        volume: 1.0
    };

    let pretty = PrettyConfig::new()
        .depth_limit(5)
        .separate_tuple_members(true)
        .decimal_floats(true);

    if let Ok(_res) = fs::write("./assets/settings/config.ron", &to_string_pretty(&settings, pretty).unwrap()) {
        settings
    } else {
        panic!("COULD NOT SAVE FILE");
    }
}

fn setup(
    mut commands: Commands,
    mut app_state: ResMut<State<AppState>>,
    loading_text_query: Query<Entity, With<LoadingText>>,
    sound_assets: Res<SoundAssets>,
    audio: Res<Audio>,
    settings: Res<GameSettings>,
) {
    // remove loading text
    let loading_text_entity = loading_text_query.single();
    commands.entity(loading_text_entity).despawn_recursive();
    // camera 2d
    commands.spawn_bundle(OrthographicCameraBundle::new_2d()).insert(Camera2D);
    // move user to main menu
    app_state.set(AppState::MainMenu).unwrap();
    // play song
    audio.set_volume(settings.volume.clone());
    audio.play_looped(sound_assets.main_menu.clone());
}