use bevy::prelude::*;
use crate::{AppState, CameraUI};
use crate::asset_loader::{FontAssets, TextureAssets};
use bevy::app::AppExit;

pub struct MainMenuPlugin;

enum MenuButtonType {
    // main
    Play,
    Settings,
    Quit,
    // settings
    Resolution,
    Sound,
    Credits,
    Back,
}

#[derive(Component)]
struct MenuButtonText;

#[derive(Component)]
struct MenuButton(MenuButtonType);

#[derive(Component)]
struct MenuButtonNodeBundle;

#[derive(Component)]
struct MenuBackground;

impl Plugin for MainMenuPlugin {
    fn build(&self, app: &mut App) {
        app
            .add_system_set(SystemSet::on_enter(AppState::MainMenu)
                .with_system(spawn_main_menu)
            )
            .add_system_set(SystemSet::on_update(AppState::MainMenu)
                .with_system(button_coloring_menu)
                .with_system(button_handling_menu)
            )
            .add_system_set(SystemSet::on_exit(AppState::MainMenu)
                .with_system(despawn_menu)
            )
            .add_system_set(SystemSet::on_enter(AppState::Settings)
                .with_system(spawn_settings)
            )
            .add_system_set(SystemSet::on_update(AppState::Settings)
                .with_system(button_coloring_menu)
                .with_system(button_handling_menu)
            )
            .add_system_set(SystemSet::on_exit(AppState::Settings)
                .with_system(despawn_menu)
            );
    }
}

fn spawn_main_menu(
    mut commands: Commands,
    font_assets: Res<FontAssets>,
    texture_assets: Res<TextureAssets>,
    camera_ui_query: Query<Entity, With<CameraUI>>,
) {
    // spawn buttons
    let play_button = spawn_return_list_button(
        &mut commands, &font_assets,
        "PLAY".to_string(),
        MenuButtonType::Play,
    );
    let settings_button = spawn_return_list_button(
        &mut commands, &font_assets,
        "Settings".to_string(),
        MenuButtonType::Settings,
    );
    let quit_button = spawn_return_list_button(
        &mut commands, &font_assets,
        "Quit".to_string(),
        MenuButtonType::Quit,
    );
    // grouping buttons into list
    let button_grouper = get_side_node(&mut commands);
    commands.entity(button_grouper).push_children(&[play_button, settings_button, quit_button]);
    spawn_background(&mut commands, &camera_ui_query, &texture_assets);
}

fn spawn_settings(
    mut commands: Commands,
    font_assets: Res<FontAssets>,
    texture_assets: Res<TextureAssets>,
    camera_ui_query: Query<Entity, With<CameraUI>>,
) {
    let res_button = spawn_return_list_button(
        &mut commands, &font_assets,
        "Resolution".to_string(),
        MenuButtonType::Resolution,
    );
    let sound_button = spawn_return_list_button(
        &mut commands, &font_assets,
        "Sound".to_string(),
        MenuButtonType::Sound,
    );
    let credits_button = spawn_return_list_button(
        &mut commands, &font_assets,
        "Credits".to_string(),
        MenuButtonType::Credits,
    );
    let back_button = spawn_return_list_button(
        &mut commands, &font_assets,
        "Back".to_string(),
        MenuButtonType::Back,
    );
    // grouping buttons into list
    let button_group = get_side_node(&mut commands);
    commands.entity(button_group).push_children(&[res_button, sound_button, credits_button, back_button]);
    spawn_background(&mut commands, &camera_ui_query, &texture_assets);
}

fn despawn_menu(
    mut commands: Commands,
    menu_query: Query<Entity, With<MenuButtonNodeBundle>>,
    background_query: Query<Entity, With<MenuBackground>>,
) {
    let menu_entity = menu_query.single();
    commands.entity(menu_entity).despawn_recursive();
    let background_entity = background_query.single();
    commands.entity(background_entity).despawn();
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
    interaction_query: Query<
        (&Interaction, &MenuButton),
        Changed<Interaction>
    >,
    mut event_writer: EventWriter<AppExit>,
    mut app_state: ResMut<State<AppState>>,
) {
    for (interaction, button_type) in interaction_query.iter() {
        if interaction == &Interaction::Clicked {
            match button_type.0 {
                MenuButtonType::Quit => {
                    event_writer.send(AppExit);
                }
                MenuButtonType::Play => {
                    todo!();
                }
                MenuButtonType::Settings => {
                    app_state.set(AppState::Settings).unwrap();
                }
                MenuButtonType::Back => {
                    app_state.set(AppState::MainMenu).unwrap();
                }
                _ => {
                    todo!();
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
// spawn background in bg camera
fn spawn_background(
    commands: &mut Commands,
    camera_ui_query: &Query<Entity, With<CameraUI>>,
    texture_assets: &Res<TextureAssets>,
) {
    let cam = camera_ui_query.single();
    commands.entity(cam).with_children(|parent| {
        parent
            .spawn_bundle(SpriteBundle {
                texture: texture_assets.main_menu_background.clone(),
                transform: Transform {
                    scale: Vec3::new(4.0, 4.0, 1.0),
                    ..Default::default()
                },
                ..Default::default()
            })
            .insert(MenuBackground);
    });
}

// HELPER function
// NOT SYSTEM
// creates node bundle for buttons in menu on left side
fn get_side_node(
    commands: &mut Commands
) -> Entity {
    commands
        .spawn_bundle(NodeBundle {
            style: Style {
                flex_direction: FlexDirection::ColumnReverse,
                margin: Rect {
                    left: Val::Px(16.0),
                    right: Val::Px(16.0),
                    ..Default::default()
                },
                ..Default::default()
            },
            color: UiColor::from(Color::rgba(0.0, 0.0, 0.0, 0.4)),
            ..Default::default()
        })
        .insert(MenuButtonNodeBundle)
        .id()
}