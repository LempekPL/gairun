use bevy::prelude::{Component, Plugin, Vec2};
use crate::App;
// components that could be used in more than one file

#[derive(Component)]
pub struct Coords(pub Vec2);

#[derive(Component)]
pub struct Hitbox(pub Vec2);

pub struct GlobalPlugin;

impl Plugin for GlobalPlugin {
    fn build(&self, app: &mut App) {
        todo!()
    }
}

// systems that could be used in more than one file