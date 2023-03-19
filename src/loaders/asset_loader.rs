use bevy::prelude::*;
use bevy_asset_loader::prelude::*;
use bevy_kira_audio::AudioSource;
use crate::global::AppState;

#[derive(AssetCollection, Resource)]
pub struct FontAssets {
    #[asset(path = "fonts/open_sans/OpenSans-Regular.ttf")]
    pub open_sans_regular: Handle<Font>,
    #[asset(path = "fonts/open_sans/OpenSans-Bold.ttf")]
    pub open_sans_bold: Handle<Font>,
}

#[derive(AssetCollection, Resource)]
pub struct AudioAssets {
    #[asset(path = "sounds/main_menu.flac")]
    pub main_menu: Handle<AudioSource>,
}

#[derive(AssetCollection, Resource)]
pub struct TextureAssets {
    #[asset(path = "textures/main_menu_background.png")]
    pub main_menu_background: Handle<Image>,
    #[asset(path = "textures/gairun.png")]
    pub gairun_title: Handle<Image>,
    #[asset(path = "textures/gairun_walk_test.png")]
    pub gairun_walk_test: Handle<Image>,
}

pub struct AssetLoaderPlugin;

impl Plugin for AssetLoaderPlugin {
    fn build(&self, app: &mut App) {
        app.add_state::<AppState>();
        app.add_loading_state(
            LoadingState::new(AppState::LoadingAssets)
                .continue_to_state(AppState::MenuMain)
        );
        app.add_collection_to_loading_state::<_, FontAssets>(AppState::LoadingAssets);
        app.add_collection_to_loading_state::<_, AudioAssets>(AppState::LoadingAssets);
        app.add_collection_to_loading_state::<_, TextureAssets>(AppState::LoadingAssets);
    }
}
