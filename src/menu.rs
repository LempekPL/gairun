use bevy::app::AppExit;
use bevy::prelude::*;
use crate::AppState;
use crate::asset_loader::FontAssets;
use crate::InGameState::{Playing, Paused};
use crate::MainMenus::Main;

pub struct InGameMenuPlugin;

enum MenuButtonType {
    Continue,
    ToMainMenu,
    Quit,
}

#[derive(Component)]
struct MenuButtonText;

#[derive(Component)]
struct MenuButton(MenuButtonType);

#[derive(Component)]
struct MenuButtonNodeBundle;

#[derive(Component)]
struct MenuBackground;

impl Plugin for InGameMenuPlugin {
    fn build(&self, app: &mut App) {
        app
            .add_system_set(SystemSet::on_update(AppState::Game(Playing))
                .with_system(check_for_un_pause)
            )
            .add_system_set(SystemSet::on_enter(AppState::Game(Paused))
                .with_system(spawn_menu)
            )
            .add_system_set(SystemSet::on_update(AppState::Game(Paused))
                .with_system(button_coloring_menu)
                .with_system(button_handling_menu)
                .with_system(check_for_un_pause)
            )
            .add_system_set(SystemSet::on_exit(AppState::Game(Paused))
                .with_system(despawn_menu)
            );
    }
}

fn check_for_un_pause(
    mut keys: ResMut<Input<KeyCode>>,
    mut app_state: ResMut<State<AppState>>,
) {
    if keys.just_pressed(KeyCode::Escape) {
        if app_state.current() == &AppState::Game(Playing) {
            app_state.push(AppState::Game(Paused)).unwrap();
            keys.reset(KeyCode::Escape);
        } else if app_state.current() == &AppState::Game(Paused) {
            app_state.pop().unwrap();
            keys.reset(KeyCode::Escape);
        }
    }
}

fn spawn_menu(
    mut commands: Commands,
    font_assets: Res<FontAssets>,
) {
    // spawn buttons
    let continue_button = spawn_return_list_button(
        &mut commands, &font_assets,
        "Continue".to_string(),
        MenuButtonType::Continue,
    );
    let menu_button = spawn_return_list_button(
        &mut commands, &font_assets,
        "Main Menu".to_string(),
        MenuButtonType::ToMainMenu,
    );
    let quit_button = spawn_return_list_button(
        &mut commands, &font_assets,
        "Quit".to_string(),
        MenuButtonType::Quit,
    );
    // grouping buttons into list
    let button_grouper = get_side_node(&mut commands);
    commands.entity(button_grouper).push_children(&[continue_button, menu_button, quit_button]);
}

fn despawn_menu(
    mut commands: Commands,
    menu_query: Query<Entity, With<MenuButtonNodeBundle>>,
    background_query: Query<Entity, With<MenuBackground>>,
) {
    let e_menu = menu_query.get_single();
    if let Ok(e_menu) = e_menu {
        commands.entity(e_menu).despawn_recursive();
    }
    let e_background = background_query.get_single();
    if let Ok(e_background) = e_background {
        commands.entity(e_background).despawn();
    }
}

// system for changing color for a button when user hovers over it or clicks it
#[allow(clippy::type_complexity)]
fn button_coloring_menu(
    mut interaction_query: Query<
        (&Interaction, &mut UiColor, &Children),
        (Changed<Interaction>, With<MenuButton>),
    >,
    mut button_font_weight_query: Query<&mut Text, With<MenuButtonText>>,
    font_assets: Res<FontAssets>,
) {
    for (interaction, mut color, children_ids) in interaction_query.iter_mut() {
        let mut button_text = button_font_weight_query.get_mut(children_ids[0]).unwrap();
        match interaction {
            Interaction::Clicked => {
                *color = Color::YELLOW.into();
                button_text.sections[0].style.font = font_assets.open_sans_bold.clone();
            }
            Interaction::Hovered => {
                *color = Color::GREEN.into();
                button_text.sections[0].style.font = font_assets.open_sans_bold.clone();
            }
            Interaction::None => {
                *color = Color::DARK_GREEN.into();
                button_text.sections[0].style.font = font_assets.open_sans_regular.clone();
            }
        }
    }
}

fn button_handling_menu(
    q_interaction: Query<
        (&Interaction, &MenuButton),
        Changed<Interaction>
    >,
    mut ev_app_exit: EventWriter<AppExit>,
    mut app_state: ResMut<State<AppState>>,
) {
    for (interaction, button_type) in q_interaction.iter() {
        if interaction == &Interaction::Clicked {
            match button_type.0 {
                MenuButtonType::Quit => {
                    ev_app_exit.send(AppExit);
                }
                MenuButtonType::Continue => {
                    app_state.pop().unwrap();
                }
                MenuButtonType::ToMainMenu => {
                    app_state.replace(AppState::MainMenu(Main)).unwrap();
                }
                _ => {
                    panic!("Not supposed to happened");
                }
            }
        }
    }
}

// HELPER function
// NOT SYSTEM
// commands is the Commands bevy struct
// font_assets are font assets loaded in main.rs struct
// name for display name
// type for button type (aka what it will trigger)
// returns entity
fn spawn_return_list_button(commands: &mut Commands, font_assets: &Res<FontAssets>, name: String, button_type: MenuButtonType) -> Entity {
    commands
        .spawn_bundle(ButtonBundle {
            style: Style {
                // size button
                size: Size::new(Val::Px(150.0), Val::Px(65.0)),
                // center button
                margin: Rect::all(Val::Auto),
                // horizontally center child text
                justify_content: JustifyContent::Center,
                // vertically center child text
                align_items: AlignItems::Center,
                ..Default::default()
            },
            color: Color::WHITE.into(),
            ..Default::default()
        })
        .with_children(|parent| {
            parent.spawn_bundle(TextBundle {
                text: Text::with_section(
                    name,
                    TextStyle {
                        font: font_assets.open_sans_regular.clone(),
                        font_size: 40.0,
                        color: Color::WHITE,
                    },
                    Default::default(),
                ),
                ..Default::default()
            }).insert(MenuButtonText);
        })
        .insert(MenuButton(button_type))
        .id()
}

// HELPER function
// NOT SYSTEM
// creates node bundle for buttons in menu center
fn get_side_node(
    commands: &mut Commands
) -> Entity {
    commands
        .spawn_bundle(NodeBundle {
            style: Style {
                flex_direction: FlexDirection::ColumnReverse,
                margin: Rect {
                    left: Val::Auto,
                    right: Val::Auto,
                    ..Default::default()
                },
                size: Size::new(Val::Percent(100.0), Val::Percent(100.0)),
                ..Default::default()
            },
            color: UiColor::from(Color::rgba(0.5, 0.5, 0.5, 0.1)),
            ..Default::default()
        })
        .insert(MenuButtonNodeBundle)
        .id()
}