use bevy::prelude::*;
use crate::global::{Coords, Hitbox};

#[derive(Bundle)]
pub struct BlockBundle {
    pub coords: Coords,
    pub hitbox: Hitbox,
    pub sprite: SpriteSheetBundle,
}