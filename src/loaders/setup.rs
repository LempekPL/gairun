use bevy::prelude::*;
use crate::global::AppState;
use super::preload::LoadingText;

pub fn setup(
    mut commands: Commands,
    loading_text_query: Query<Entity, With<LoadingText>>,
) {
    // remove loading text
    let loading_text_entity = loading_text_query.single();
    commands.entity(loading_text_entity).despawn_recursive();
    info!("Setup is Done");
}