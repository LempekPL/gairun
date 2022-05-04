use std::collections::HashMap;
use std::fs;
use bevy::prelude::*;
use serde::{Deserialize, Serialize};
use crate::{EventWriter, GlobalScale};
use crate::mapper::{LoadMapEvent, MapComponent};
use crate::mapper::blocks::BlockConfig;
use crate::toasts::ToastEvent;

pub fn generate_map(
    mut commands: Commands,
    mut ev_gen_map: EventReader<LoadMapEvent>,
    mut ev_toast: EventWriter<ToastEvent>,
    q_map_entity: Query<Entity, With<MapComponent>>,
    r_gs: Res<GlobalScale>,
    asset_server: Res<AssetServer>,
) {
    // get first map to be in event
    let map = ev_gen_map.iter().next();
    if let Some(map) = map {
        let map_entity = q_map_entity.get_single();
        // remove existing map
        if let Ok(map_entity) = map_entity {
            commands.entity(map_entity).despawn_recursive();
        }
        let map_entity = commands.spawn().insert(MapComponent).id();

        // start loading new map
        let path = get_path(true, &map.pack, &format!("maps/{}", map.collection), &map.name);
        if let Ok(map_config) = fs::read_to_string(format!("{}.ron", path)) {
            let map_config: Result<MapConfig, _> = ron::from_str(&map_config);
            if let Ok(map_config) = map_config {
                // insert map data as resource
                commands.insert_resource(map_config.clone());

                // load blocks
                let mut blocks_data: HashMap<String, TempBlock> = HashMap::new();
                for (block_id, path_string) in map_config.used_blocks.iter() {
                    let path = get_path_from_string(true, path_string.to_string());

                    if let Ok(block_config) = fs::read_to_string(format!("{}.ron", path)) {
                        let block_config: Result<BlockConfig, _> = ron::from_str(&block_config);

                        if let Ok(block_config) = block_config {
                            let texture_path = get_path_custom(false, block_config.texture.path, "textures");
                            let texture: Handle<Image> = asset_server.load(&format!("{}.png", texture_path)).clone();
                            blocks_data.insert(block_id.to_owned(), TempBlock {
                                texture: TempBlockTexture {
                                    image: texture,
                                    width: block_config.texture.width,
                                    height: block_config.texture.height,
                                },
                                width: block_config.hitbox.0,
                                height: block_config.hitbox.1,
                            });
                        } else {
                            todo!("Display info toast 3 (block wRONg formatting)")
                        }
                    } else {
                        todo!("Display info toast 4 (block file not found)")
                    }
                }

                //spawn blocks
                for (i, row) in map_config.game_map.iter().enumerate() {
                    for (j, block_id) in row.iter().enumerate() {
                        if !block_id.is_empty() {
                            let block_data = blocks_data.get(block_id);
                            if let Some(block_data) = block_data {
                                let block = commands.spawn_bundle(SpriteBundle {
                                    transform: Transform {
                                        translation: Vec3::new(j as f32 * 16.0 * 2., -(i as f32 * 16.0 * 2.), 1.0),
                                        scale: r_gs.0,
                                        ..Default::default()
                                    },
                                    texture: block_data.texture.image.clone(),
                                    ..Default::default()
                                }).id();
                            }
                        }
                        // commands.entity(map_entity).push_children(&[block]);
                    }
                }
            } else {
                todo!("Display error toast 1 (wRONg formatting)")
            }
        } else {
            todo!("Display error toast 2 (file not found)")
        }
    }
}

// helper function
fn send_error(mut ev: EventWriter<ToastEvent>, error: String) {
    ev.send(ToastEvent {
        text: error,
        text_color: Color::WHITE,
        background_color: Color::ORANGE,
        font: None,
    });
}

// map file destruct
#[serde(rename_all = "camelCase")]
#[derive(Deserialize, Serialize, Clone, Debug)]
struct MapConfig {
    player_settings: PlayerSettings,
    used_blocks: HashMap<String, String>,
    required_packs: Vec<String>,
    game_map: Vec<Vec<String>>,
}

#[serde(rename_all = "camelCase")]
#[derive(Deserialize, Serialize, Clone, Debug)]
struct PlayerSettings {
    spawn: PlayerSpawn,
    invisible: bool,
    controllability: bool,
    noclip: bool,
}

#[derive(Deserialize, Serialize, Clone, Debug)]
enum PlayerSpawn {
    Point(String),
    Coords(u32, u32),
}
//

// temporal block data
#[derive(Clone, Debug)]
struct TempBlock {
    texture: TempBlockTexture,
    width: f32,
    height: f32,
}

#[derive(Clone, Debug)]
struct TempBlockTexture {
    image: Handle<Image>,
    width: u32,
    height: u32,
}

// helper function
fn get_path(full: bool, pack: &str, place: &str, name: &str) -> String {
    if pack == "gairun" {
        let pre = if full { "./assets/" } else { "" };
        format!("{}{}/{}", pre, place, name)
    } else {
        let pre = if full { "./custom_packs/" } else { "" };
        format!("{}{}/{}/{}", pre, pack, place, name)
    }
}

// helper function
fn get_path_from_string(full: bool, path_string: String) -> String {
    let pack: Vec<&str> = path_string.split(":").collect();
    let place: Vec<&str> = pack[1].split("/").collect();
    get_path(full, pack[0], place[0], place[1])
}

// helper function
fn get_path_custom(full: bool, path_string: String, custom: &str) -> String {
    let pack: Vec<&str> = path_string.split(":").collect();
    let place: Vec<&str> = pack[1].split("/").collect();
    get_path(full, pack[0], &format!("{}/{}", custom, place[0]), place[1])
}