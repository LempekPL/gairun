use std::fs;
use bevy::prelude::*;
use bevy::utils::HashMap;
use bevy::sprite::Rect;
use serde::{Deserialize, Serialize};
use crate::{EventWriter, GlobalScale};
use crate::global::{Coords, Hitbox};
use crate::mapper::{LoadMapEvent, MapComponent};
use crate::mapper::blocks::{BlockBundle};
use crate::toasts::ToastEvent;

pub fn generate_map(
    mut commands: Commands,
    mut ev_gen_map: EventReader<LoadMapEvent>,
    mut ev_toast: EventWriter<ToastEvent>,
    q_map_entity: Query<Entity, With<MapComponent>>,
    r_gs: Res<GlobalScale>,
    mut r_tex_atlas: ResMut<Assets<TextureAtlas>>,
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

                    // read block data
                    if let Ok(block_config) = fs::read_to_string(format!("{}.ron", path)) {
                        let block_config: Result<BlockConfig, _> = ron::from_str(&block_config);

                        // create temporary blocks to get spawned
                        if let Ok(block_config) = block_config {
                            // TODO: check for animation
                            let texture_path = get_path_custom(false, block_config.texture.path, "textures");
                            let texture: Handle<Image> = asset_server.load(&format!("{}.png", texture_path)).clone();
                            let texture= r_tex_atlas.add(TextureAtlas::from_grid(
                                texture,
                                Vec2::new(block_config.texture.width,block_config.texture.height),
                                1,1
                            ));
                            blocks_data.insert(block_id.to_owned(), TempBlock {
                                texture,
                                width: block_config.hitbox.0,
                                height: block_config.hitbox.1,
                            });
                        } else {
                            // create error texture instead of crashing
                            todo!("Display info toast 3 (block wRONg formatting)")
                        }
                    } else {
                        // create error texture instead of crashing
                        todo!("Display info toast 4 (block file not found)")
                    }
                }

                //spawn blocks
                for (i, row) in map_config.game_map.iter().enumerate() {
                    for (j, block_id) in row.iter().enumerate() {
                        if !block_id.is_empty() {
                            let block_data = blocks_data.get(block_id);
                            if let Some(block_data) = block_data {
                                let block = commands.spawn_bundle(BlockBundle {
                                    coords: Coords(Vec2::new(j as f32, i as f32)),
                                    hitbox: Hitbox(Vec2::new(block_data.width as f32, block_data.height as f32)),
                                    sprite: SpriteSheetBundle {
                                        transform: Transform {
                                            translation: Vec3::new(j as f32 * 16.0 * 2., -(i as f32 * 16.0 * 2.), 1.0),
                                            scale: r_gs.0,
                                            ..Default::default()
                                        },
                                        texture_atlas: block_data.texture.clone(),
                                        ..Default::default()
                                    }
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

// map file destruct
#[derive(Deserialize, Serialize, Clone, Debug)]
#[serde(rename_all = "camelCase")]
struct MapConfig {
    player_settings: PlayerSettings,
    used_blocks: HashMap<String, String>,
    required_packs: Vec<String>,
    game_map: Vec<Vec<String>>,
}

#[derive(Deserialize, Serialize, Clone, Debug)]
#[serde(rename_all = "camelCase")]
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

// block file struct
#[derive(Deserialize, Serialize, Clone, Debug)]
pub struct BlockConfig {
    pub hitbox: (f32, f32),
    pub texture: BlockTexture,
}

#[derive(Deserialize, Serialize, Clone, Debug)]
pub struct BlockTexture {
    pub path: String,
    animation: Option<BlockAnimation>,
    pub width: f32,
    pub height: f32,
}

#[derive(Deserialize, Serialize, Clone, Debug)]
struct BlockAnimation {
    speed: String,
    frames: u32,
    frame_order: Vec<u32>,
}
//

// temporal block data
#[derive(Clone, Debug)]
struct TempBlock {
    texture: Handle<TextureAtlas>,
    width: f32,
    height: f32,
}