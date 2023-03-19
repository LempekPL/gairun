use bevy::prelude::*;
use bevy_console::{reply, AddConsoleCommand, ConsoleCommand, ConsolePlugin};
use crate::debug::IsDebug;
use crate::entity::{Flying, Noclip};
use crate::mapper::LoadMapEvent;
use clap::Parser;

pub struct DebugConsolePlugin;

impl Plugin for DebugConsolePlugin {
    fn build(&self, app: &mut App) {
        app.add_plugin(ConsolePlugin);

        // command systems
        app.add_console_command::<DebugCommand, _>(debug_command);
        app.add_console_command::<NoclipCommand, _>(noclip_command);
        app.add_console_command::<FlyCommand, _>(fly_command);
        app.add_console_command::<MapCommand, _>(map_command);
    }
}

// commands

// Turns on/off debug lines
#[derive(Parser, ConsoleCommand)]
#[command(name = "debug")]
struct DebugCommand {
    option: Option<bool>,
}

fn debug_command(
    mut log: ConsoleCommand<DebugCommand>,
    mut r_debug: ResMut<IsDebug>,
) {
    if let Some(Ok(DebugCommand { option })) = log.take() {
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
#[derive(Parser, ConsoleCommand)]
#[command(name = "noclip")]
struct NoclipCommand {
    option: Option<bool>,
}

fn noclip_command(
    mut log: ConsoleCommand<NoclipCommand>,
    mut q_entity: Query<&mut Noclip>,
) {
    if let Some(Ok(NoclipCommand { option })) = log.take() {
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
#[derive(Parser, ConsoleCommand)]
#[command(name = "fly")]
struct FlyCommand {
    option: Option<bool>,
}

fn fly_command(
    mut log: ConsoleCommand<FlyCommand>,
    mut q_entity: Query<&mut Flying>,
) {
    if let Some(Ok(FlyCommand { option })) = log.take() {
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
#[derive(Parser, ConsoleCommand)]
#[command(name = "map")]
struct MapCommand {
    pack: String,
    collection: String,
    name: String,
}

fn map_command(
    mut log: ConsoleCommand<MapCommand>,
    mut ev_map_gen: EventWriter<LoadMapEvent>,
) {
    if let Some(Ok(MapCommand { pack, collection, name, })) = log.take() {
        log.ok();
        ev_map_gen.send(LoadMapEvent {
            pack,
            collection,
            name,
        });
    }
}