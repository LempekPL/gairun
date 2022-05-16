use bevy::prelude::*;
use bevy_kira_audio::Audio;
use crate::global::AppState;
use crate::asset_loader::SoundAssets;
use crate::settings::GameSettings;

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