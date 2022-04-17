use bevy::prelude::*;
use crate::{AppState, MainMenus};
use super::preload::LoadingText;

pub(super) fn setup(
    mut commands: Commands,
    mut app_state: ResMut<State<AppState>>,
    loading_text_query: Query<Entity, With<LoadingText>>,
) {
    // remove loading text
    let loading_text_entity = loading_text_query.single();
    commands.entity(loading_text_entity).despawn_recursive();
    // move user to main menu
    app_state.set(AppState::MainMenu(MainMenus::Main)).unwrap();
    // create 2d camera
    commands.spawn_bundle(OrthographicCameraBundle::new_2d()).insert(Camera2D);
}