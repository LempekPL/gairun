use bevy::prelude::*;
// use bevy_inspector_egui::WorldInspectorParams;
use crate::global::AppState;

#[derive(Component)]
pub struct LoadingText;

pub(super) fn preload(
    mut commands: Commands,
    mut app_state: ResMut<NextState<AppState>>,
    asset_server: Res<AssetServer>,
    // mut world_inspector_params: ResMut<WorldInspectorParams>
) {
    commands.spawn(Camera2dBundle::default())
        .insert(UiCameraConfig { show_ui: true });
    // loading text spawn
    commands.spawn(NodeBundle {
        style: Style {
            size: Size::new(Val::Percent(100.0), Val::Percent(100.0)),
            position_type: PositionType::Absolute,
            justify_content: JustifyContent::Center,
            align_items: AlignItems::FlexEnd,
            ..Default::default()
        },
        background_color: BackgroundColor(Color::BLACK),
        ..Default::default()
    }).with_children(|parent| {
        parent.spawn(TextBundle {
            style: Style {
                align_self: AlignSelf::Center,
                ..Default::default()
            },
            text: Text::from_section(
                "LOADING",
                TextStyle {
                    font: asset_server.load("fonts/open_sans/OpenSans-Bold.ttf"),
                    font_size: 80.0,
                    color: Color::WHITE,
                }
            ),
            ..Default::default()
        });
    }).insert(LoadingText);
    // todo
    // disable world egui
    // world_inspector_params.enabled = false;
}