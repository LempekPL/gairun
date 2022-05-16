mod map_generator;
mod blocks;

use bevy::prelude::*;
use crate::global::AppState;
use crate::global::InGameState::Playing;
use crate::mapper::map_generator::generate_map;

pub struct MapPlugin;

impl Plugin for MapPlugin {
    fn build(&self, app: &mut App) {
        app
            .add_event::<LoadMapEvent>()
            // .add_startup_system(create_map_parent)
            .add_system_set(SystemSet::on_update(AppState::Game(Playing))
                .with_system(generate_map)
            );
    }
}

#[derive(Component)]
pub struct MapComponent;

// event
pub struct LoadMapEvent {
    pub pack: String,
    pub collection: String,
    pub name: String,
}

pub fn clear_map(
    mut commands: Commands,
    q_map_entity: Query<Entity, With<MapComponent>>,
) {
    let map_entity = q_map_entity.get_single();
    if let Ok(map_entity) = map_entity {
        commands.entity(map_entity).despawn_recursive();
        commands.spawn().insert(MapComponent);
    }
}