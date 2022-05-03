mod preload;
mod setup;
mod setup_settings;
mod setup_icon;
mod setup_sound;
mod setup_window;

use bevy::prelude::{App, Plugin, SystemSet};
use crate::AppState;
use self::setup::setup;
use self::preload::preload;
use self::setup_settings::setup_settings;
use self::setup_icon::setup_icon;
use self::setup_sound::setup_sound;
use self::setup_window::setup_window;

pub struct LoaderPlugin;

impl Plugin for LoaderPlugin {
    fn build(&self, app: &mut App) {
        // forcing preload before anything else
        app.add_state(AppState::Preload);
        app.add_startup_system(setup_icon);
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
        app.add_system_set(SystemSet::on_enter(AppState::Loading(3))
            .with_system(setup_window)
        );
    }
}