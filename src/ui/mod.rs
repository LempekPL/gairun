pub mod menus;
pub mod toasts;

use bevy::prelude::*;
use bevy::ui::FocusPolicy;
use menus::MenuPlugin;
use toasts::ToastsPlugin;

#[derive(Component)]
pub struct ToastLayer;

#[derive(Component)]
pub struct MenuLayer;

#[derive(Component)]
pub struct LayerUi;

pub struct UiPlugin;

impl Plugin for UiPlugin {
    fn build(&self, app: &mut App) {
        app.add_plugin(MenuPlugin);
        app.add_plugin(ToastsPlugin);
        app.add_startup_system(create_ui_layers);
    }
}

fn create_ui_layers(
    mut commands: Commands
) {
    commands.spawn_bundle(NodeBundle {
        style: Style {
            size: Size::new(Val::Percent(100.0), Val::Percent(100.0)),
            ..default()
        },
        color: Color::NONE.into(),
        ..default()
    })
        .insert(LayerUi)
        .insert(Name::new("LayersUi"))
        .with_children(|root| {

            // menu layer
            root.spawn_bundle(NodeBundle {
                color: Color::NONE.into(),
                ..default()
            })
                .insert(MenuLayer)
                .insert(Name::new("Menu"));

            // toasts layer
            root.spawn_bundle(NodeBundle {
                style: Style {
                    display: Display::Flex,
                    size: Size::new(Val::Percent(100.0), Val::Percent(100.0)),
                    position_type: PositionType::Absolute,
                    justify_content: JustifyContent::FlexEnd,
                    align_items: AlignItems::Center,
                    flex_direction: FlexDirection::Column,
                    align_self: AlignSelf::Center,
                    ..default()
                },
                focus_policy: FocusPolicy::Pass,
                color: Color::NONE.into(),
                ..default()
            }).insert(ToastLayer).insert(Name::new("Toasts"));
        });
}