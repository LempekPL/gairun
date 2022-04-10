use std::time::Duration;
use bevy::prelude::*;

pub struct ToastsPlugin;

#[derive(Debug)]
pub struct ToastEvent {
    pub text: String,
    pub text_color: Color,
    pub background_color: Color,
    pub font: Handle<Font>,
}

#[derive(Component)]
struct ToastList;

#[derive(Component)]
struct ToastTimer(Timer);

impl Plugin for ToastsPlugin {
    fn build(&self, app: &mut App) {
        app
            .add_event::<ToastEvent>()
            .add_startup_system(create_toast_node)
            .add_system(spawn_toast)
            .add_system(clear_toast);
    }
}

fn create_toast_node(
    mut commands: Commands,
) {
    commands.spawn_bundle(NodeBundle {
        style: Style {
            display: Display::Flex,
            size: Size::new(Val::Percent(100.0), Val::Percent(100.0)),
            position_type: PositionType::Absolute,
            justify_content: JustifyContent::FlexEnd,
            align_items: AlignItems::Center,
            flex_direction: FlexDirection::Column,
            align_self: AlignSelf::Center,
                ..Default::default()
        },
        color: UiColor(Color::NONE),
        ..Default::default()
    }).insert(ToastList);
}

fn spawn_toast(
    mut commands: Commands,
    mut ev_toasts: EventReader<ToastEvent>,
    mut q: Query<Entity, With<ToastList>>,
) {
    let toast_list = q.single_mut();
    for ev in ev_toasts.iter() {
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
                            font: ev.font.clone(),
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