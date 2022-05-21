use std::time::Duration;
use bevy::ecs::system::QuerySingleError;
use bevy::prelude::*;
use bevy::ui::FocusPolicy;
use crate::ui::ToastLayer;

pub struct ToastsPlugin;

#[derive(Debug)]
pub struct ToastEvent {
    pub text: String,
    pub text_color: Color,
    pub background_color: Color,
    pub font: Option<Handle<Font>>,
}

#[derive(Component)]
struct ToastTimer(Timer);

impl Plugin for ToastsPlugin {
    fn build(&self, app: &mut App) {
        app
            .add_event::<ToastEvent>()
            .add_system(spawn_toast)
            .add_system(clear_toast);
    }
}

fn spawn_toast(
    mut commands: Commands,
    mut ev_toasts: EventReader<ToastEvent>,
    mut q: Query<Entity, With<ToastLayer>>,
    asset_server: Res<AssetServer>,
) {
    let toast_list = match q.get_single_mut() {
        Ok(ent) => {ent}
        Err(_) => {return}
    };
    for ev in ev_toasts.iter() {
        let font: Handle<Font> = if ev.font.is_some() {
            ev.font.as_ref().unwrap().clone()
        } else {
            asset_server.load("fonts/open_sans/OpenSans-Regular.ttf")
        };
        let button_entity = commands.
            spawn_bundle(NodeBundle {
                style: Style {
                    size: Size::new(Val::Percent(60.0), Val::Px(50.0)),
                    align_items: AlignItems::FlexStart,
                    padding: Rect { left: Val::Px(16.0), ..Default::default() },
                    margin: Rect { top: Val::Px(16.0), ..Default::default() },
                    ..Default::default()
                },
                color: UiColor(ev.background_color),
                ..Default::default()
            })
            .with_children(|parent| {
                parent.spawn_bundle(TextBundle {
                    style: Style {
                        align_self: AlignSelf::Center,
                        ..Default::default()
                    },
                    text: Text::with_section(
                        &ev.text,
                        TextStyle {
                            color: ev.text_color,
                            font_size: 25.0,
                            font: font.clone(),
                        },
                        Default::default(),
                    ),
                    ..Default::default()
                });
            })
            .insert(ToastTimer(Timer::new(Duration::from_secs(5), false)))
            .id();
        commands.entity(toast_list).add_child(button_entity);
    };
}

fn clear_toast(
    mut commands: Commands,
    time: Res<Time>,
    mut q: Query<(Entity, &mut ToastTimer)>,
) {
    for (entity, mut toast_timer) in q.iter_mut() {
        toast_timer.0.tick(time.delta());

        if toast_timer.0.finished() {
            commands.entity(entity).despawn_recursive();
        }
    }
}