use bevy::prelude::*;
use bevy_inspector_egui::{Inspectable, InspectorPlugin, RegisterInspectable};
use crate::IsDebug;

// components that could be used in more than one file

#[derive(Component, Inspectable)]
pub struct Coords(pub Vec2);

#[derive(Component, Inspectable)]
pub struct Hitbox(pub Vec2);

#[derive(Component)]
pub struct GloballyScaled;

pub struct GlobalPlugin;

impl Plugin for GlobalPlugin {
    fn build(&self, app: &mut App) {
        app.register_inspectable::<Hitbox>();
        app.register_inspectable::<Coords>();

        app.insert_resource(IsDebug(false));
        app.insert_resource(GlobalScale::default());
        #[cfg(debug_assertions)]
        app.add_plugin(InspectorPlugin::<GlobalScale>::new());
        app.add_system(change_global_scale_coords);
        app.add_system(change_global_scale_translate);
    }
}

// global systems

fn change_global_scale_coords(
    mut q_globally_scaled: Query<(&mut GlobalTransform, &Coords), With<GloballyScaled>>,
    r_gs: Res<GlobalScale>,
) {
    for (mut tran, coords) in q_globally_scaled.iter_mut() {
        tran.scale = r_gs.0;
        tran.translation = Vec3::new(coords.0.x * 16. * r_gs.0.x, coords.0.y * 16. * r_gs.0.y,0.0)
    }
}

fn change_global_scale_translate(
    mut q_globally_scaled: Query<(&mut Transform), With<GloballyScaled>>,
    r_gs: Res<GlobalScale>,
) {
    for (mut tran) in q_globally_scaled.iter_mut() {
        tran.scale = r_gs.0;
        tran.translation = Vec3::new(tran.translation.x * 16. * r_gs.0.x, tran.translation.y * 16. * r_gs.0.y,0.0)
    }
}

// global states and other

#[derive(Clone, Copy, Inspectable)]
pub struct GlobalScale(
    #[inspectable(min = Vec3::new(0.1, 0.1, 0.1), max = Vec3::new(4., 4., 4.))]
    pub Vec3
);


impl Default for GlobalScale {
    fn default() -> Self {
        Self(Vec3::new(2.,2.,2.))
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

