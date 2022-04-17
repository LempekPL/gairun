use bevy::prelude::*;
use bevy::window::WindowId;
use bevy::winit::WinitWindows;
use winit::window::Icon;

pub fn setup_window(
    windows: NonSend<WinitWindows>,
    mut window: ResMut<Windows>,
) {
    let primary = windows.get_window(WindowId::primary()).unwrap();
    let (icon_rgba, icon_width, icon_height) = {
        let image = image::open("icon.png")
            .expect("Failed to open icon path")
            .into_rgba8();
        let (width, height) = image.dimensions();
        let rgba = image.into_raw();
        (rgba, width, height)
    };
    let icon = Icon::from_rgba(icon_rgba, icon_width, icon_height).unwrap();
    primary.set_window_icon(Some(icon));
    let monitor = primary.primary_monitor().unwrap();
    // set window position to be in the middle of primary screen
    let primary_game = window.get_primary_mut().unwrap();
    let w_pos = ((monitor.size().width as f32 - primary_game.width()) / 2.0) as i32;
    let h_pos = ((monitor.size().height as f32 - primary_game.height()) / 2.0) as i32;
    primary_game.set_position(IVec2::new(w_pos, h_pos));
}