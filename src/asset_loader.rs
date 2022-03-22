use bevy::prelude::*;
use bevy_asset_loader::{AssetLoader, AssetCollection};
use bevy_kira_audio::AudioSource;
use crate::AppState;

#[derive(AssetCollection)]
pub struct FontAssets {
    #[asset(path = "fonts/open_sans/OpenSans-Regular.ttf")]
    pub open_sans_regular: Handle<Font>,
    #[asset(path = "fonts/open_sans/OpenSans-Bold.ttf")]
    pub open_sans_bold: Handle<Font>,
}

#[derive(AssetCollection)]
pub struct SoundAssets {
    #[asset(path = "sounds/main_menu.wav")]
    pub main_menu: Handle<AudioSource>,
}

#[derive(AssetCollection)]
pub struct TextureAssets {
    #[asset(path = "textures/main_menu_background.png")]
    pub main_menu_background: Handle<Image>,
}

pub struct AssetLoaderPlugin;

impl Plugin for AssetLoaderPlugin {
    fn build(&self, app: &mut App) {
        AssetLoader::new(AppState::LoadingAssets)
            .continue_to_state(AppState::Loading)
            .with_collection::<FontAssets>()
            .with_collection::<SoundAssets>()
            .with_collection::<TextureAssets>()
            .build(app);
    }
}
