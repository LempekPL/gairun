use std::fs;
use bevy::prelude::*;
use ron::ser::{PrettyConfig, to_string_pretty};
use crate::{AppState, GameKeys, GameSettings};
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
    let (game_settings, error) = load_settings("./assets/settings/config.ron", GameSettings::default());
    commands.insert_resource(game_settings);
    // display toast notification if user can't save settings file
    if error {
        ev_toast.send(ToastEvent {
            text: "Can't save settings file".to_string(),
            text_color: Color::WHITE,
            background_color: Color::RED,
            font: font_assets.open_sans_regular.clone(),
        });
    }
    // check for game key settings
    let (game_keys, error_keys) = load_settings("./assets/settings/keys.ron", GameKeys::default());
    commands.insert_resource(game_keys);
    // display toast notification if user can't save key settings file
    if error_keys {
        ev_toast.send(ToastEvent {
            text: "Can't save settings keys file".to_string(),
            text_color: Color::WHITE,
            background_color: Color::RED,
            font: font_assets.open_sans_regular.clone(),
        });
    }
    // set window size
    win_desc.width = game_settings.resolution.0;
    win_desc.height = game_settings.resolution.1;
    // move user to next loading step
    app_state.set(AppState::Loading(1)).unwrap();
}

// HELPER function, NOT system
fn load_settings<T>(settings_path: &str, default_settings: T) -> (T, bool)
    where
        T: serde::Serialize + serde::de::DeserializeOwned
{
    if let Ok(saved_settings) = fs::read_to_string(settings_path) {
        let settings: Result<T, _> = ron::from_str(&saved_settings);
        if let Ok(settings) = settings {
            save_settings(settings_path, settings)
        } else {
            save_settings(settings_path, default_settings)
        }
    } else {
        save_settings(settings_path, default_settings)
    }
}

// HELPER function, NOT system
fn save_settings<T: serde::Serialize>(settings_path: &str, settings: T) -> (T, bool) {
    let pretty = PrettyConfig::new()
        .depth_limit(5)
        .separate_tuple_members(true)
        .decimal_floats(true);

    if let Ok(_res) = fs::write(settings_path, &to_string_pretty(&settings, pretty).unwrap()) {
        (settings, false)
    } else {
        (settings, true)
    }
}