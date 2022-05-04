
use serde::{Deserialize, Serialize};

// #[derive(Bundle)]
// struct BlockBundle {
//     transform: Transform
// }


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
    pub width: u32,
    pub height: u32,
}

#[derive(Deserialize, Serialize, Clone, Debug)]
struct BlockAnimation {
    speed: String,
    frames: u32,
    frame_order: Vec<u32>,
}
//