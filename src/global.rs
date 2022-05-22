use bevy::prelude::*;
use bevy_inspector_egui::{Inspectable, InspectorPlugin, RegisterInspectable};
use crate::IsDebug;

// components that could be used in more than one file

#[derive(Component, Inspectable)]
pub struct Coords(pub Vec2);

#[derive(Component, Inspectable)]
pub struct Hitbox(pub Vec2);

pub struct GlobalPlugin;

impl Plugin for GlobalPlugin {
    fn build(&self, app: &mut App) {
        app.register_inspectable::<Hitbox>();
        app.register_inspectable::<Coords>();

        app.insert_resource(IsDebug(false));
        app.insert_resource(GlobalScale::default());
    }
}

// global systems


// global states and other

#[derive(Clone, Copy)]
pub struct GlobalScale(
    pub Vec3
);

impl Default for GlobalScale {
    fn default() -> Self {
        Self(Vec3::new(4., 4., 1.))
    }
}

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

// defaults

impl Default for Coords {
    fn default() -> Self {
        Self(Default::default())
    }
}

impl Default for Hitbox {
    fn default() -> Self {
        Self(Default::default())
    }
}

