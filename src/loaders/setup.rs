use bevy::prelude::*;
use crate::global::AppState;
use super::preload::LoadingText;

pub fn setup(
    mut commands: Commands,
    mut app_state: ResMut<State<AppState>>,
    loading_text_query: Query<Entity, With<LoadingText>>,
) {
    // remove loading text
    let loading_text_entity = loading_text_query.single();
    commands.entity(loading_text_entity).despawn_recursive();
    // move user to main menu
    app_state.set(AppState::Loading(3)).unwrap();
    // create 2d camera
    commands.spawn_bundle(OrthographicCameraBundle::new_2d());
}