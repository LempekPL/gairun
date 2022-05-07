use bevy::prelude::{Bundle, SpriteSheetBundle};
use serde::{Deserialize, Serialize};
use crate::global::{Coords, Hitbox};

#[derive(Bundle)]
pub struct BlockBundle {
    pub coords: Coords,
    pub hitbox: Hitbox,
    #[bundle]
    pub sprite: SpriteSheetBundle,
}