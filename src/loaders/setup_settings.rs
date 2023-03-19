use std::fs;
use bevy::prelude::*;
use bevy::window::{PrimaryWindow, WindowResolution};
use ron::ser::{PrettyConfig, to_string_pretty};
use crate::global::AppState;
use crate::settings::{GameKeybinds, GameSettings};
use crate::ui::toasts::ToastEvent;

pub fn setup_settings(
    mut commands: Commands,
    mut app_state: ResMut<NextState<AppState>>,
    mut ev_toast: EventWriter<ToastEvent>,
    mut primary_query: Query<&mut Window, With<PrimaryWindow>>
) {
    // check for game settings
    // main game settings
    let (game_settings, err_settings) = load_settings::<GameSettings>("./assets/settings/config.ron");
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
    let (game_keybinds, err_keybinds) = load_settings::<GameKeybinds>("./assets/settings/keys.ron");
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
    info!("Settings Setup is Done");

    // set window settings
    let Ok(mut primary) = primary_query.get_single_mut() else { return; };
    primary.mode = game_settings.get_mode();
    primary.resolution = game_settings.resolution.into();
    info!("Window Setup is Done");
    app_state.set(AppState::LoadingAssets);
}

// HELPER function, NOT system
fn load_settings<T>(settings_path: &str) -> (T, bool)
    where
        T: serde::Serialize + serde::de::DeserializeOwned + std::default::Default
{
    if let Ok(saved_settings) = fs::read_to_string(settings_path) {
        let settings: Result<T, _> = ron::from_str(&saved_settings);
        if let Ok(settings) = settings {
            save_settings(settings_path, settings)
        } else {
            save_settings(settings_path, T::default())
        }
    } else {
        save_settings(settings_path, T::default())
    }
}

// HELPER function, NOT system
fn save_settings<T: serde::Serialize>(settings_path: &str, settings: T) -> (T, bool) {
    // makes ron files look better
    let pretty = PrettyConfig::new()
        .depth_limit(5)
        .separate_tuple_members(true);

    if fs::write(settings_path, &to_string_pretty(&settings, pretty).unwrap()).is_ok() {
        (settings, false)
    } else {
        (settings, true)
    }
}