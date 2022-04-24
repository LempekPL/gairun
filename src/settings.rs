use bevy::prelude::KeyCode;
use bevy::window::WindowMode;
use serde::{Deserialize, Serialize};

#[derive(Deserialize, Serialize, Copy, Clone)]
pub struct GameKeybinds {
    #[serde(default = "DEF_KEY_UP")]
    pub up: KeyCode,
    #[serde(default = "DEF_KEY_DOWN")]
    pub down: KeyCode,
    #[serde(default = "DEF_KEY_LEFT")]
    pub left: KeyCode,
    #[serde(default = "DEF_KEY_RIGHT")]
    pub right: KeyCode,
}

const DEF_KEY_UP: fn() -> KeyCode = || KeyCode::W;
const DEF_KEY_DOWN: fn() -> KeyCode = || KeyCode::S;
const DEF_KEY_LEFT: fn() -> KeyCode = || KeyCode::A;
const DEF_KEY_RIGHT: fn() -> KeyCode = || KeyCode::D;

#[derive(Deserialize, Serialize, Copy, Clone)]
pub struct GameSettings {
    #[serde(default = "DEF_SET_VOL")]
    pub volume: f32,
    #[serde(default = "DEF_SET_RES")]
    pub resolution: (f32, f32),
    // 0 - Windowed
    // 1 - BorderlessFullscreen
    // 2 - SizedFullscreen
    // 3 - Fullscreen
    #[serde(default = "DEF_SET_MOD")]
    pub mode: u8,
}

const DEF_SET_VOL: fn() -> f32 = || 1.0;
const DEF_SET_RES: fn() -> (f32, f32) = || (1280.0, 720.0);
const DEF_SET_MOD: fn() -> u8 = || 3;

impl Default for GameKeybinds {
    fn default() -> Self {
        Self {
            up: KeyCode::W,
            down: KeyCode::S,
            left: KeyCode::A,
            right: KeyCode::D,
        }
    }
}

impl GameSettings {
    pub fn get_mode(self) -> WindowMode {
        match self.mode {
            0 => WindowMode::Windowed,
            1 => WindowMode::BorderlessFullscreen,
            2 => WindowMode::SizedFullscreen,
            _ => WindowMode::Fullscreen,
        }
    }
}

impl Default for GameSettings {
    fn default() -> Self {
        Self {
            volume: 1.0,
            resolution: (1280.0, 720.0),
            mode: 3,
        }
    }
}