#![windows_subsystem = "windows"]

mod asset_loader;
mod camera;
mod debug;
mod entity;
mod global;
mod loaders;
mod mapper;
mod settings;
mod ui;

use bevy::prelude::*;
use bevy_console::{AddConsoleCommand, ConsoleCommand, ConsolePlugin, reply};
use bevy_inspector_egui::WorldInspectorPlugin;
use bevy_kira_audio::AudioPlugin;
use bevy_prototype_debug_lines::DebugLinesPlugin;
use crate::asset_loader::AssetLoaderPlugin;
use crate::camera::CameraPlugin;
use crate::debug::{DebugPlugin, IsDebug};
use crate::entity::{EntityPlugin, Flying, Noclip};
use crate::global::GlobalPlugin;
use crate::loaders::LoaderPlugin;
use crate::mapper::{LoadMapEvent, MapPlugin};
use crate::ui::UiPlugin;

fn main() {
    let mut app = App::new();
    app.insert_resource(ClearColor(Color::BLACK));
    app.insert_resource(WindowDescriptor {
        title: "gairun".to_string(),
        resizable: false,
        width: 1920.0,
        height: 1080.0,
        ..Default::default()
    });
    app.add_plugins(DefaultPlugins);
    app.add_plugin(AudioPlugin);
    app.add_plugin(DebugLinesPlugin::default());
    // do not change these around, both won't work otherwise (this is why commands are here)
    // own implementation of console needed
    app.add_plugin(ConsolePlugin);
    app.add_plugin(WorldInspectorPlugin::new());
    //

    app.add_plugin(GlobalPlugin);
    app.add_plugin(AssetLoaderPlugin);
    app.add_plugin(LoaderPlugin);
    app.add_plugin(EntityPlugin);
    app.add_plugin(CameraPlugin);
    app.add_plugin(MapPlugin);
    app.add_plugin(UiPlugin);
    app.add_plugin(DebugPlugin);

    // command systems
    app.add_console_command::<DebugCommand, _, _>(debug_command);
    app.add_console_command::<NoclipCommand, _, _>(noclip_command);
    app.add_console_command::<FlyCommand, _, _>(fly_command);
    app.add_console_command::<MapCommand, _, _>(map_command);

    app.run();
}

// commands

// Turns on/off debug lines
#[derive(ConsoleCommand)]
#[console_command(name = "debug")]
struct DebugCommand {
    option: Option<bool>,
}

fn debug_command(
    mut log: ConsoleCommand<DebugCommand>,
    mut r_debug: ResMut<IsDebug>,
) {
    if let Some(DebugCommand { option }) = log.take() {
        log.ok();
        match option {
            Some(true) => {
                r_debug.0 = true;
                reply!(log, "Debug turned on");
            }
            Some(false) => {
                r_debug.0 = false;
                reply!(log, "Debug turned off");
            }
            None => {
                r_debug.0 = !r_debug.0;
                let what = if r_debug.0 { "on" } else { "off" };
                reply!(log, "Debug turned {}", what);
            }
        }
    }
}

// Turns on/off noclip
#[derive(ConsoleCommand)]
#[console_command(name = "noclip")]
struct NoclipCommand {
    option: Option<bool>,
}

fn noclip_command(
    mut log: ConsoleCommand<NoclipCommand>,
    mut q_entity: Query<&mut Noclip>,
) {
    if let Some(NoclipCommand { option }) = log.take() {
        match q_entity.get_single_mut() {
            Ok(mut ent) => {
                log.ok();
                match option {
                    Some(true) => {
                        ent.is_noclip = true;
                        reply!(log, "Noclip turned on");
                    }
                    Some(false) => {
                        ent.is_noclip = false;
                        reply!(log, "Noclip turned off");
                    }
                    None => {
                        ent.is_noclip = !ent.is_noclip;
                        let what = if ent.is_noclip { "on" } else { "off" };
                        reply!(log, "Noclip turned {}", what);
                    }
                }
            }
            Err(ent_err) => {
                log.failed();
                reply!(log, "{:?}",ent_err);
            }
        }
    }
}

// Turns on/off fly
#[derive(ConsoleCommand)]
#[console_command(name = "fly")]
struct FlyCommand {
    option: Option<bool>,
}

fn fly_command(
    mut log: ConsoleCommand<FlyCommand>,
    mut q_entity: Query<&mut Flying>,
) {
    if let Some(FlyCommand { option }) = log.take() {
        match q_entity.get_single_mut() {
            Ok(mut ent) => {
                log.ok();
                match option {
                    Some(true) => {
                        ent.is_flying = true;
                        reply!(log, "Fly turned on");
                    }
                    Some(false) => {
                        ent.is_flying = false;
                        reply!(log, "Fly turned off");
                    }
                    None => {
                        ent.is_flying = !ent.is_flying;
                        let what = if ent.is_flying { "on" } else { "off" };
                        reply!(log, "Fly turned {}", what);
                    }
                }
            }
            Err(ent_err) => {
                log.failed();
                reply!(log, "{:?}",ent_err);
            }
        }
    }
}

// Changes map
#[derive(ConsoleCommand)]
#[console_command(name = "map")]
struct MapCommand {
    pack: String,
    collection: String,
    name: String,
}

fn map_command(
    mut log: ConsoleCommand<MapCommand>,
    mut ev_map_gen: EventWriter<LoadMapEvent>,
) {
    if let Some(MapCommand { pack, collection, name, }) = log.take() {
        log.ok();
        ev_map_gen.send(LoadMapEvent {
            pack,
            collection,
            name,
        });
    }
}