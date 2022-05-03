use bevy::prelude::*;
use crate::{AppState, MainMenus};
use crate::settings::GameSettings;
use bevy::window::WindowId;
use bevy::winit::WinitWindows;

pub fn setup_window(
    mut app_state: ResMut<State<AppState>>,
    mut window: ResMut<Windows>,
    game_settings: Res<GameSettings>,
) {
    // set window settings
    let window = window.get_primary_mut().unwrap();
    window.set_mode(game_settings.get_mode());
    window.set_resolution(game_settings.resolution.0, game_settings.resolution.1);
    window.set_position(IVec2::new(0,0));

    app_state.set(AppState::MainMenu(MainMenus::Main)).unwrap();
}