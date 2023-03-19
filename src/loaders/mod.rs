mod preload;
mod setup;
mod setup_settings;
mod setup_icon;
mod asset_loader;

use bevy::prelude::*;
use crate::global::AppState;
use self::setup::setup;
use self::preload::preload;
use self::setup_settings::setup_settings;
use self::setup_icon::setup_icon;
pub use self::asset_loader::*;

pub struct LoaderPlugin;

impl Plugin for LoaderPlugin {
    fn build(&self, app: &mut App) {
        app.add_plugin(AssetLoaderPlugin);
        app.add_system(preload.on_startup());
        app.add_system(setup_icon.in_schedule(OnEnter(AppState::SetupIcon)));
        app.add_systems((setup_settings, setup).in_schedule(OnEnter(AppState::SetupSystems)));
    }
}
