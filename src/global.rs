use bevy::prelude::{Component, Plugin, Vec2};
use crate::{App, Vec3};
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


// global states and other

#[derive(Clone, Copy)]
pub struct GlobalScale(pub Vec3);

#[derive(Clone, Eq, PartialEq, Debug, Hash)]
pub enum AppState {
    // loading
    Preload,
    LoadingAssets,
    Loading(u8),
    // main menu
    Menu(MenuState),
    // in-game
    Game(InGameState),
}

#[derive(Clone, Eq, PartialEq, Debug, Hash)]
pub enum MenuState {
    Main,
    Settings,
    Credit,
}

#[derive(Clone, Eq, PartialEq, Debug, Hash)]
pub enum InGameState {
    Playing,
    Paused(PausedState),
}

#[derive(Clone, Eq, PartialEq, Debug, Hash)]
pub enum PausedState {
    Main,
    Settings,
}
