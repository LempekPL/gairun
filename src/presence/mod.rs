use bevy::prelude::{Plugin, ResMut, SystemSet};
use bevy_discord_presence::config::{RPCConfig, RPCPlugin};
use bevy_discord_presence::state::ActivityState;
use discord_presence::models::ActivityAssets;
use crate::{App, AppState};
use crate::MainMenus::{Main, Settings};

pub struct DiscordPlugin;

impl Plugin for DiscordPlugin {
    fn build(&self, app: &mut App) {
        app.add_plugin(RPCPlugin(
            RPCConfig {
                app_id: 971525507541790720,
                show_time: true,
            }
        ));
        app.add_system_set(SystemSet::on_enter(AppState::Loading(0))
            .with_system(up_loading)
        );
        app.add_system_set(SystemSet::on_enter(AppState::MainMenu(Main))
            .with_system(up_menu)
        );
        app.add_system_set(SystemSet::on_enter(AppState::MainMenu(Settings(0)))
            .with_system(up_settings)
        );
    }
}

fn up_loading(mut state: ResMut<ActivityState>) {
    state.details = Some("Loading".to_string());
    state.assets = Some(ActivityAssets {
        large_image: Some("icon".to_string()),
        large_text: Some("gairun".to_string()),
        small_image: None,
        small_text: None,
    });
}

fn up_menu(mut state: ResMut<ActivityState>) {
    state.details = Some("Main Menu".to_string());
}

fn up_settings(mut state: ResMut<ActivityState>) {
    state.details = Some("Settings".to_string());
}