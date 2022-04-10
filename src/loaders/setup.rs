use bevy::prelude::*;
use bevy_kira_audio::Audio;
use crate::{AppState, GameSettings};
use crate::asset_loader::SoundAssets;
use super::preload::LoadingText;

pub(super) fn setup_sound(
    mut app_state: ResMut<State<AppState>>,
    audio: Res<Audio>,
    sound_assets: Res<SoundAssets>,
    settings: Res<GameSettings>,
) {
    // play song
    audio.set_volume(settings.volume);
    audio.play_looped(sound_assets.main_menu.clone());
    // move user to next loading step
    app_state.set(AppState::Loading(2)).unwrap();
}

pub(super) fn setup(
    mut commands: Commands,
    mut app_state: ResMut<State<AppState>>,
    loading_text_query: Query<Entity, With<LoadingText>>,
) {
    // remove loading text
    let loading_text_entity = loading_text_query.single();
    commands.entity(loading_text_entity).despawn_recursive();
    // move user to main menu
    app_state.set(AppState::MainMenu).unwrap();
}