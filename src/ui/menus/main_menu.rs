use bevy::app::AppExit;
use bevy::prelude::*;
use crate::asset_loader::{FontAssets, TextureAssets};
use crate::global::{AppState};
use crate::global::InGameState::Playing;
use crate::global::MenuState::{Credit, MainMenu, Settings};
use crate::mapper::LoadMapEvent;
use crate::ui::MenuLayer;
use crate::ui::menus::*;

pub struct MainMenuPlugin;

impl Plugin for MainMenuPlugin {
    fn build(&self, app: &mut App) {
        // main menu
        app
            .add_system_set(SystemSet::on_enter(AppState::Menu(MainMenu))
                .with_system(spawn_menu)
            )
            .add_system_set(SystemSet::on_update(AppState::Menu(MainMenu))
                .with_system(button_coloring)
                .with_system(button_handler_menu)
            )
            .add_system_set(SystemSet::on_exit(AppState::Menu(MainMenu))
                .with_system(despawn_ui_node_recursive)
            );
        // settings
        // app
        //     .add_system_set(SystemSet::on_enter(AppState::Menu(Settings))
        //                         .with_system(spawn_settings)
        //                     // .with_system()
        //     )
        //     .add_system_set(SystemSet::on_update(AppState::Menu(Settings))
        //         .with_system(button_coloring)
        //         .with_system(button_handler_settings)
        //     )
        //     .add_system_set(SystemSet::on_exit(AppState::Menu(Settings))
        //         .with_system(despawn_settings)
        //     );
    }
}

#[derive(Component)]
enum ButtonType {
    Play,
    ToSettings,
    ToMain,
    Quit,
}

#[derive(Component)]
struct GairunTitle;

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
    let play_button = create_button(
        &mut commands,
        font_assets.open_sans_regular.clone(),
        "PLAY".to_string(),
        ButtonType::Play,
    );
    let settings_button = create_button(
        &mut commands,
        font_assets.open_sans_regular.clone(),
        "Settings".to_string(),
        ButtonType::ToSettings,
    );
    let quit_button = create_button(
        &mut commands,
        font_assets.open_sans_regular.clone(),
        "Quit".to_string(),
        ButtonType::Quit,
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
        color: UiColor::from(Color::rgba(0.0, 0.0, 0.0, 0.4)),
        ..default()
    };
    let grouper = get_custom_ui_node(&mut commands, node_bundle, "MainMenuGrouper".to_string());
    commands.entity(menu).add_child(grouper);
    commands.entity(grouper).push_children(&[gairun_title, play_button, settings_button, quit_button]);
}

fn button_handler_menu(
    q_interaction: Query<
        (&Interaction, &ButtonType),
        Changed<Interaction>
    >,
    mut ev_app_exit: EventWriter<AppExit>,
    mut app_state: ResMut<State<AppState>>,
    mut ev_map_gen: EventWriter<LoadMapEvent>,
) {
    for (interaction, button_type) in q_interaction.iter() {
        if interaction == &Interaction::Clicked {
            match button_type {
                ButtonType::Quit => {
                    ev_app_exit.send(AppExit);
                }
                ButtonType::Play => {
                    ev_map_gen.send(LoadMapEvent {
                        pack: "gairun".to_string(),
                        collection: "collection".to_string(),
                        name: "1".to_string(),
                    });
                    app_state.set(AppState::Game(Playing)).unwrap();
                }
                ButtonType::ToSettings => {
                    // app_state.set(AppState::Menu(Settings)).unwrap();
                }
                _ => {
                    app_state.set(AppState::Menu(MainMenu)).unwrap();
                }
            }
        }
    }
}