use bevy::prelude::*;
use crate::loaders::{FontAssets, TextureAssets};
use crate::global::AppState;
use crate::ui::MenuLayer;
use crate::ui::menus::*;

pub struct InGameMenuPlugin;

impl Plugin for InGameMenuPlugin {
    fn build(&self, app: &mut App) {
        app.add_system(check_for_un_pause.in_set(OnUpdate(AppState::GamePlaying)));
        app.add_system(spawn_menu.in_schedule(OnEnter(AppState::GamePaused)));
        app.add_systems((button_coloring, button_handler_menu, check_for_un_pause).in_set(OnUpdate(AppState::GamePaused)));
        app.add_system(despawn_ui_node_recursive.in_schedule(OnExit(AppState::GamePaused)));

        // in-game main
        // app
        //     .add_system_set(SystemSet::on_update(AppState::Game(Playing))
        //         .with_system(check_for_un_pause)
        //     )
        //     .add_system_set(SystemSet::on_enter(AppState::GamePaused)
        //         .with_system(spawn_menu)
        //     )
        //     .add_system_set(SystemSet::on_update(AppState::GamePaused)
        //         .with_system(button_coloring)
        //         .with_system(button_handler_menu)
        //         .with_system(check_for_un_pause)
        //     )
        //     .add_system_set(SystemSet::on_exit(AppState::GamePaused)
        //         .with_system(despawn_ui_node_recursive)
        //     );
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
    mut app_state: ResMut<NextState<AppState>>,
) {
    if keys.just_pressed(KeyCode::Escape) {
        let Some(state) = &app_state.0 else {
            return;
        };
        if state == &AppState::GamePlaying {
            app_state.set(AppState::GamePaused);
            keys.reset(KeyCode::Escape);
        } else if state == &AppState::GamePaused {
            app_state.set(AppState::GamePlaying);
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
    let gairun_title = commands.spawn(ImageBundle {
        style: Style {
            flex_shrink: 2.,
            margin: UiRect {
                top: Val::Px(-50.),
                bottom: Val::Px(-50.),
                ..default()
            },
            ..default()
        },
        image: UiImage {
            texture: texture_assets.gairun_title.clone(),
            flip_x: false,
            flip_y: false,
        },
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
            margin: UiRect {
                bottom: Val::Px(40.),
                ..default()
            },
            ..default()
        },
        background_color: BackgroundColor(Color::rgba(0.0, 0.0, 0.0, 0.5)),
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
    mut app_state: ResMut<NextState<AppState>>,
) {
    for (interaction, button_type) in q_interaction.iter() {
        if interaction == &Interaction::Clicked {
            match button_type {
                ButtonType::Continue => {
                    app_state.set(AppState::GamePlaying);
                }
                ButtonType::ToMain => {
                    app_state.set(AppState::MenuMain);
                }
                _ => {
                    app_state.set(AppState::MenuMain);
                }
            }
        }
    }
}