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
	}

	public static class FixtureAtlas {

		public static final String Obstacles = "ObstaclesFixtureAtlas";

	}
	
	public static class XmlDocuments {

		public static final String Obstacles = "ObstaclesXmlDocument";

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

		resource(FixtureAtlas.Obstacles, new FixtureAtlasResourceBuilder() //
				.shapeFile(internal("data/fixtures/obstacles.bin")));
		
		resource(XmlDocuments.Obstacles, xmlDocument("data/fixtures/obstacles.svg"));

	}
}
