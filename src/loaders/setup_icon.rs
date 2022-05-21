use bevy::prelude::*;
use bevy::window::WindowId;
use bevy::winit::WinitWindows;
use winit::window::Icon;
use winit::platform::windows::WindowExtWindows;
use crate::ui::toasts::ToastEvent;

pub fn setup_icon(
    windows: NonSend<WinitWindows>,
    mut ev_toast: EventWriter<ToastEvent>,
) {
    let primary = windows.get_window(WindowId::primary()).unwrap();
    let (icon_rgba, icon_width, icon_height) = {
        let image = match image::open("assets/resources/icon.png") {
            Ok(image) => { image.into_rgba8() }
            Err(_) => {
                ev_toast.send(ToastEvent {
                    text: "Couldn't load icon".to_string(),
                    text_color: Color::WHITE,
                    background_color: Color::ORANGE,
                    font: None,
                });
                return;
            }
        };
        let (width, height) = image.dimensions();
        let rgba = image.into_raw();
        (rgba, width, height)
    };
    let icon = Icon::from_rgba(icon_rgba, icon_width, icon_height).unwrap();
    primary.set_window_icon(Some(icon.clone()));
    primary.set_taskbar_icon(Some(icon));
}