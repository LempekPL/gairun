use std::fs;
use bevy::prelude::*;
use ron::ser::{PrettyConfig, to_string_pretty};
use crate::{AppState, GameSettings};
use crate::asset_loader::FontAssets;
use crate::toasts::ToastEvent;

pub(super) fn setup_settings(
    mut commands: Commands,
    mut app_state: ResMut<State<AppState>>,
    font_assets: Res<FontAssets>,
    mut ev_toast: EventWriter<ToastEvent>,
    mut win_desc: ResMut<WindowDescriptor>,
) {
    // check for game settings
    let (settings, error) = load_settings();
    commands.insert_resource(settings);
    // display toast notification if user can't save settings file
    if error {
        ev_toast.send(ToastEvent {
            text: "Can't save settings file".to_string(),
            text_color: Color::WHITE,
            background_color: Color::RED,
            font: font_assets.open_sans_regular.clone(),
        });
    }
    // set window size
    win_desc.width = settings.resolution.0;
    win_desc.height = settings.resolution.1;
    // move user to next loading step
    app_state.set(AppState::Loading(1)).unwrap();
}

// HELPER function, NOT system
fn load_settings() -> (GameSettings, bool) {
    if let Ok(saved_settings) = fs::read_to_string("./assets/settings/config.ron") {
        let settings: Result<GameSettings, _> = ron::from_str(&saved_settings);
        if let Ok(settings) = settings {
            save_settings(settings)
        } else {
            create_settings()
        }
    } else {
        create_settings()
    }
}

// HELPER function, NOT system
fn create_settings() -> (GameSettings, bool) {
    let settings: GameSettings = Default::default();
    save_settings(settings)
}

// HELPER function, NOT system
fn save_settings(settings: GameSettings) -> (GameSettings, bool) {
    let pretty = PrettyConfig::new()
        .depth_limit(5)
        .separate_tuple_members(true)
        .decimal_floats(true);

    if let Ok(_res) = fs::write("./assets/settings/config.ron", &to_string_pretty(&settings, pretty).unwrap()) {
        (settings, false)
    } else {
        (settings, true)
    }
}