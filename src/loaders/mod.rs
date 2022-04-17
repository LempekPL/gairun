mod preload;
mod setup;
mod setup_settings;
mod setup_window;
mod setup_sound;

use bevy::prelude::*;
use crate::AppState;
use self::setup::setup;
use self::preload::preload;
use self::setup_settings::setup_settings;
use self::setup_window::setup_window;
use self::setup_sound::setup_sound;

pub struct LoaderPlugin;

impl Plugin for LoaderPlugin {
    fn build(&self, app: &mut App) {
        // forcing preload before anything else
        app.add_state(AppState::Preload);
        app.add_startup_system(setup_window);
        app.add_startup_system(preload);
        // initiating setup after asset loading
        app.add_system_set(SystemSet::on_enter(AppState::Loading(0))
            .with_system(setup_settings)
        );
        app.add_system_set(SystemSet::on_enter(AppState::Loading(1))
            .with_system(setup_sound)
        );
        app.add_system_set(SystemSet::on_enter(AppState::Loading(2))
            .with_system(setup)
        );
    }
}