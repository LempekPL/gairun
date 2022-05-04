use std::fs;
use bevy::prelude::*;
use ron::ser::{PrettyConfig, to_string_pretty};
use crate::AppState;
use crate::settings::{GameKeybinds, GameSettings};
use crate::toasts::ToastEvent;

pub fn setup_settings(
    mut commands: Commands,
    mut app_state: ResMut<State<AppState>>,
    mut ev_toast: EventWriter<ToastEvent>,
) {
    // check for game settings
    // main game settings
    let (game_settings, err_settings) = load_settings("./assets/settings/config.ron", GameSettings::default());
    // if there are any toasts send event
    if err_settings {
        ev_toast.send(ToastEvent {
            text: "Can't save settings file".to_string(),
            text_color: Color::WHITE,
            background_color: Color::RED,
            font: None,
        });
    }
    commands.insert_resource(game_settings);
    // keybindings
    let (game_keybinds, err_keybinds) = load_settings("./assets/settings/keys.ron", GameKeybinds::default());
    // if there are any toasts send event
    if err_keybinds {
        ev_toast.send(ToastEvent {
            text: "Can't save keybindings file".to_string(),
            text_color: Color::WHITE,
            background_color: Color::RED,
            font: None,
        });
    }
    commands.insert_resource(game_keybinds);

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
    // makes ron files look better
    let pretty = PrettyConfig::new()
        .depth_limit(5)
        .separate_tuple_members(true)
        .decimal_floats(true);

    if fs::write(settings_path, &to_string_pretty(&settings, pretty).unwrap()).is_ok() {
        (settings, false)
    } else {
        (settings, true)
    }
}