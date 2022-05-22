use bevy::prelude::*;
use crate::asset_loader::{FontAssets, TextureAssets};
use crate::global::{AppState};
use crate::global::AppState::Menu;
use crate::global::InGameState::{Paused, Playing};
use crate::global::MenuState::{Credit, MainMenu, Settings};
use crate::global::PausedState::InMain;
use crate::mapper::LoadMapEvent;
use crate::ui::MenuLayer;
use crate::ui::menus::*;

pub struct InGameMenuPlugin;

impl Plugin for InGameMenuPlugin {
    fn build(&self, app: &mut App) {
        // in-game main
        app
            .add_system_set(SystemSet::on_update(AppState::Game(Playing))
                .with_system(check_for_un_pause)
            )
            .add_system_set(SystemSet::on_enter(AppState::Game(Paused(InMain)))
                .with_system(spawn_menu)
            )
            .add_system_set(SystemSet::on_update(AppState::Game(Paused(InMain)))
                .with_system(button_coloring)
                .with_system(button_handler_menu)
                .with_system(check_for_un_pause)
            )
            .add_system_set(SystemSet::on_exit(AppState::Game(Paused(InMain)))
                .with_system(despawn_ui_node_recursive)
            );
    }
}

#[derive(Component)]
enum ButtonType {
    Continue,
    ToMain,
}

#[derive(Component)]
struct GairunTitle;

fn check_for_un_pause(
    mut keys: ResMut<Input<KeyCode>>,
    mut app_state: ResMut<State<AppState>>,
) {
    if keys.just_pressed(KeyCode::Escape) {
        if app_state.current() == &AppState::Game(Playing) {
            app_state.push(AppState::Game(Paused(InMain))).unwrap();
            keys.reset(KeyCode::Escape);
        } else if app_state.current() == &AppState::Game(Paused(InMain)) {
            app_state.pop().unwrap();
            keys.reset(KeyCode::Escape);
        }
    }
}

fn spawn_menu(
    mut commands: Commands,
    font_assets: Res<FontAssets>,
    texture_assets: Res<TextureAssets>,
    mut q: Query<Entity, With<MenuLayer>>,
) {
    let menu = match q.get_single_mut() {
        Ok(ent) => {ent}
        Err(_) => {return}
    };
    // spawn buttons
    let continue_button = create_button(
        &mut commands,
        font_assets.open_sans_regular.clone(),
        "Continue".to_string(),
        ButtonType::Continue,
    );
    let main_button = create_button(
        &mut commands,
        font_assets.open_sans_regular.clone(),
        "Main Menu".to_string(),
        ButtonType::ToMain,
    );
    // title
    let gairun_title = commands.spawn_bundle(ImageBundle {
        style: Style {
            flex_shrink: 2.,
            margin: Rect {
                top: Val::Px(-50.),
                bottom: Val::Px(-50.),
                ..default()
            },
            ..default()
        },
        image: UiImage(texture_assets.gairun_title.clone()),
        transform: Transform {
            scale: Vec3::new(0.5,0.5,0.5),
            ..default()
        },
        ..default()
    }).id();
    // grouping everything into list
    let node_bundle = NodeBundle {
        style: Style {
            flex_direction: FlexDirection::ColumnReverse,
            size: Size::new(Val::Percent(100.0), Val::Percent(100.0)),
            justify_content: JustifyContent::Center,
            margin: Rect {
                bottom: Val::Px(40.),
                ..default()
            },
            ..default()
        },
        color: UiColor::from(Color::rgba(0.0, 0.0, 0.0, 0.5)),
        ..default()
    };
    let grouper = get_custom_ui_node(&mut commands, node_bundle, "InGameGrouper".to_string());
    commands.entity(menu).add_child(grouper);
    commands.entity(grouper).push_children(&[gairun_title, continue_button, main_button]);
}

fn button_handler_menu(
    q_interaction: Query<
        (&Interaction, &ButtonType),
        Changed<Interaction>
    >,
    mut app_state: ResMut<State<AppState>>,
) {
    for (interaction, button_type) in q_interaction.iter() {
        if interaction == &Interaction::Clicked {
            match button_type {
                ButtonType::Continue => {
                    app_state.pop().unwrap();
                }
                ButtonType::ToMain => {
                    app_state.set(AppState::Menu(MainMenu)).unwrap();
                }
                _ => {
                    app_state.set(AppState::Menu(MainMenu)).unwrap();
                }
            }
        }
    }
}