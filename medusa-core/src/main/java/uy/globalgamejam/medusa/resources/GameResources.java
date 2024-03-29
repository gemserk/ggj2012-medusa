package uy.globalgamejam.medusa.resources;

import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.resources.ResourceManager;

/**
 * Declares all resources needed for the game.
 */
public class GameResources extends LibgdxResourceBuilder {

	public static class TextureAtlases {

		private static final String Images = "ImagesTextureAtlas";

	}

	public static class Sprites {

		public static final String Character = "CharacterSprite";
		public static final String Item = "ItemSprite";

		public static final String Obstacle0 = "Obstacle0Sprite";
		public static final String Obstacle1 = "Obstacle1Sprite";
		public static final String Muro1 = "Muro1";
		public static final String Muro2 = "Muro2";
		public static final String Enemy1 = "Enemigo1";
		public static final String Enemigo2 = "Enemigo2";
		public static final String Astronauta = "Astronauta";
		public static final String Nave = "Nave";
		public static final String Satelite = "Satelite";
		
		public static final String BottomBorder = "BottomBorderSprite";
		public static final String TopBorder = "TopBorderSprite";
		
		public static final String Cabeza = "Cabeza";
		public static final String Cuerpo = "Cuerpo";
		
		public static final String Background01 = "Background01Sprite";
		public static final String Background02 = "Background02Sprite";
		public static final String Background03 = "Background03Sprite";

	}

	public static class Animations {
		public static final String Enemy1 = "AnimationEnemy1";
		public static final String Enemy2 = "AnimationEnemy2";
	}

	public static class FixtureAtlas {

		public static final String Obstacles = "ObstaclesFixtureAtlas";

	}

	public static class XmlDocuments {

		public static final String Obstacles = "ObstaclesXmlDocument";

	}
	
	public static class MusicTracks {
		
		public static final String Game = "GameMusic";
		
	}

	public static class Audio {
		
		public static final String EatEnemy = "EatEnemySound";
		public static final String CorteCola = "CorteColaSound";
		public static final String Muerte = "MuerteSound";
		
	}

	/**
	 * Only creates all resource declarations, it doesn't load all the stuff yet.
	 */
	public static void load(ResourceManager<String> resourceManager) {
		new GameResources(resourceManager);
	}

	private GameResources(ResourceManager<String> resourceManager) {
		super(resourceManager);

		textureAtlas(TextureAtlases.Images, "data/images/pack");

		resource(Sprites.Character, sprite2() //
				.textureAtlas(TextureAtlases.Images, "character"));

		resource(Sprites.Item, sprite2() //
				.textureAtlas(TextureAtlases.Images, "item"));

		resource(Sprites.Obstacle0, sprite2() //
				.textureAtlas(TextureAtlases.Images, "obstacle-01"));

		resource(Sprites.Obstacle1, sprite2() //
				.textureAtlas(TextureAtlases.Images, "obstacle-02"));

		for (int i = 1; i < 8; i++) {
			resource("Muro" + i + "a", sprite2() //
					.textureAtlas(TextureAtlases.Images, "muro" + i + "a"));

			resource("Muro" + i + "b", sprite2() //
					.textureAtlas(TextureAtlases.Images, "muro" + i + "a").flip(true, false));
		}

		resource(Sprites.Enemy1, sprite2() //
				.textureAtlas(TextureAtlases.Images, "enemigo1a"));

		animation(Animations.Enemy1, TextureAtlases.Images, "enemigo1a", true, 200);
		animation(Animations.Enemy2, TextureAtlases.Images, "enemigo2a", true, 50);

		resource(Sprites.Astronauta, sprite2() //
				.textureAtlas(TextureAtlases.Images, "extra1a"));
		
		resource(Sprites.Nave, sprite2() //
				.textureAtlas(TextureAtlases.Images, "extra2a"));
		
		resource(Sprites.Satelite, sprite2() //
				.textureAtlas(TextureAtlases.Images, "extra3a"));

		resource(Sprites.BottomBorder, sprite2() //
				.textureAtlas(TextureAtlases.Images, "border"));

		resource(Sprites.TopBorder, sprite2() //
				.flip(false, true) //
				.textureAtlas(TextureAtlases.Images, "border"));
		
		resource(Sprites.Cabeza, sprite2() //
				.textureAtlas(TextureAtlases.Images, "cabeza"));
		
		resource(Sprites.Cuerpo, sprite2() //
				.textureAtlas(TextureAtlases.Images, "cuerpo"));
		
		resource(Sprites.Background01, sprite2() //
				.textureAtlas(TextureAtlases.Images, "background-01"));
		resource(Sprites.Background02, sprite2() //
				.textureAtlas(TextureAtlases.Images, "background-02"));
		resource(Sprites.Background03, sprite2() //
				.textureAtlas(TextureAtlases.Images, "background-03"));
		
		// resource(Sprites.Muro1, sprite2() //
		// .textureAtlas(TextureAtlases.Images, "muro1"));
		//
		// resource(Sprites.Muro2, sprite2() //
		// .textureAtlas(TextureAtlases.Images, "muro1").flip(true, false));

		resource(FixtureAtlas.Obstacles, new FixtureAtlasResourceBuilder() //
				.shapeFile(internal("data/fixtures/figuras")));

		// resource(FixtureAtlas.Obstacles, new FixtureAtlasResourceBuilder() //
		// .shapeFile(internal("data/fixtures/obstacles.bin")));

		resource(XmlDocuments.Obstacles, xmlDocument("data/fixtures/obstacles.svg"));

		music(MusicTracks.Game, "data/audio/game.ogg");
		
		sound(GameResources.Audio.EatEnemy, "data/audio/MEDUSAComer.wav");
		sound(GameResources.Audio.CorteCola, "data/audio/MEDUSACortecola.wav");
		sound(GameResources.Audio.Muerte, "data/audio/MEDUSAMuerte.wav");
	}
}
