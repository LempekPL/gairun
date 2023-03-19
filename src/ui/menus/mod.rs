mod main_menu;
mod ingame_menu;

use bevy::prelude::*;
use crate::ui::menus::ingame_menu::InGameMenuPlugin;
use crate::ui::menus::main_menu::MainMenuPlugin;

pub struct MenuPlugin;

impl Plugin for MenuPlugin {
    fn build(&self, app: &mut App) {
        app.add_plugin(MainMenuPlugin);
        app.add_plugin(InGameMenuPlugin);
    }
}

#[derive(Component)]
pub struct MenuButton;

#[derive(Component)]
pub struct UiMenu;

pub fn despawn_ui_node_recursive(
    mut commands: Commands,
    q_ui_node: Query<Entity, With<UiMenu>>,
) {
    let entity = q_ui_node.single();
    commands.entity(entity).despawn_recursive();
}

#[allow(clippy::type_complexity)]
pub fn button_coloring(
    mut q_interaction: Query<
        (&Interaction, &mut BackgroundColor),
        (Changed<Interaction>, With<MenuButton>),
    >,
) {
    for (interaction, mut color) in q_interaction.iter_mut() {
        match interaction {
            Interaction::Clicked => {
                *color = Color::YELLOW.into();
            }
            Interaction::Hovered => {
                *color = Color::GREEN.into();
            }
            Interaction::None => {
                *color = Color::DARK_GREEN.into();
            }
        }
    }
}

// helper function
pub fn get_ui_node(commands: &mut Commands, name: String) -> Entity {
    let node_bundle = NodeBundle {
        style: Style {
            flex_direction: FlexDirection::ColumnReverse,
            margin: UiRect {
                left: Val::Auto,
                right: Val::Auto,
                ..default()
            },
            ..default()
        },
        background_color: BackgroundColor(Color::rgba(0.0, 0.0, 0.0, 0.4)),
        ..default()
    };
    get_custom_ui_node(commands, node_bundle, name)
}

// helper funciton
pub fn get_custom_ui_node(commands: &mut Commands, node_bundle: NodeBundle, name: String) -> Entity {
    commands
        .spawn(node_bundle)
        .insert(UiMenu)
        .insert(Name::new(name))
        .id()
}

// helper function
pub fn create_button<T: bevy::prelude::Component>(commands: &mut Commands, font: Handle<Font>, name: String, button_type: T) -> Entity {
    commands
        .spawn(ButtonBundle {
            style: Style {
                // size button
                size: Size::new(Val::Px(200.0), Val::Px(65.0)),
                // center button
                margin: UiRect::all(Val::Auto),
                // horizontally center child text
                justify_content: JustifyContent::Center,
                // vertically center child text
                align_items: AlignItems::Center,
                ..Default::default()
            },
            background_color: Color::WHITE.into(),
            ..Default::default()
        })
        .with_children(|parent| {
            parent.spawn(TextBundle {
                text: Text::from_section(
                    &name,
                    TextStyle {
                        font: font.clone(),
                        font_size: 40.0,
                        color: Color::WHITE,
                    }
                ),
                ..Default::default()
            });
        })
        .insert(MenuButton)
        .insert(button_type)
        .insert(Name::new(format!("{} button", name)))
        .id()
}