use bevy::prelude::*;
use crate::global::AppState;
// use bevy::window::PrimaryWindow;
// use bevy::winit::WinitWindows;
// use winit::window::{Icon, WindowId};
// use winit::platform::windows::WindowExtWindows;
use crate::ui::toasts::ToastEvent;

pub fn setup_icon(
    // windows: NonSend<WinitWindows>,
    // primary_query: Query<Entity, With<PrimaryWindow>>,
    // mut commands: Commands,
    // mut ev_toast: EventWriter<ToastEvent>,
    mut app_state: ResMut<NextState<AppState>>,
) {
    // let Ok(primary) = primary_query.get_single() else { return; };
    // let primary_entity = commands.entity(primary).id();
    // let primary = windows.get_window(primary_entity).unwrap();
    // // let primary = windows.get_window(WindowId::primary()).unwrap();
    // let (icon_rgba, icon_width, icon_height) = {
    //     let image = match image::open("assets/resources/icon.png") {
    //         Ok(image) => { image.into_rgba8() }
    //         Err(_) => {
    //             ev_toast.send(ToastEvent {
    //                 text: "Couldn't load icon".to_string(),
    //                 text_color: Color::WHITE,
    //                 background_color: Color::ORANGE,
    //                 font: None,
    //             });
    //             return;
    //         }
    //     };
    //     let (width, height) = image.dimensions();
    //     let rgba = image.into_raw();
    //     (rgba, width, height)
    // };
    // let icon = Icon::from_rgba(icon_rgba, icon_width, icon_height).unwrap();
    // primary.set_window_icon(Some(icon.clone()));
    // primary.set_taskbar_icon(Some(icon));
    info!("Icon Setup is Done");
    app_state.set(AppState::SetupSystems);
}