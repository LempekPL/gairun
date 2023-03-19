use bevy::prelude::*;
// use bevy_inspector_egui::{Inspectable, RegisterInspectable};
use bevy_inspector_egui::prelude::*;
use crate::debug::IsDebug;

// components that could be used in more than one file

#[derive(Component, InspectorOptions, Reflect, Default)]
pub struct Coords(pub Vec2);

#[derive(Component, InspectorOptions, Reflect, Default)]
pub struct Hitbox(pub Vec2);

pub struct GlobalPlugin;

impl Plugin for GlobalPlugin {
    fn build(&self, app: &mut App) {
        app.register_type::<Hitbox>();
        app.register_type::<Coords>();

        app.insert_resource(IsDebug(false));
        app.insert_resource(GlobalScale::default());
    }
}

// global systems


// global states and other

#[derive(Clone, Copy, Resource)]
pub struct GlobalScale(
    pub Vec3
);

impl Default for GlobalScale {
    fn default() -> Self {
        Self(Vec3::new(2., 2., 1.))
    }
}

#[derive(Clone, Eq, PartialEq, Debug, Hash, Default, States)]
pub enum AppState {
    // loading
    #[default]
    SetupIcon,
    SetupSystems,
    LoadingAssets,
    // main menu
    MenuMain,
    MenuSettings,
    MenuCredits,
    // in-game
    GamePlaying,
    GamePaused,
    GamePausedSettings
}
