package dev.buildtool;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Random;

public class SpaceGame extends Game {
	static Random random=new Random();
	SpriteBatch batch,uiBatch;
	ArrayList<Texture> planetTextures;
	AssetManager assetManager;
	Array<StarSystem> starSystems;
	ArrayList<Texture> starTextures;
	StarShip playerShip;
	Texture redStarshipTexture;
	public Texture scalesTexture,takeOffTexture,slotTexture,alcoholTexture,toolsTexture,carPartsTexture,firearmsTexture,
			jewelleryTexture, clothesTexture,electronicsTexture,waterTexture,furnitureTexture,foodTexture,medicineTexture,
		basicProjectile,ironOreTexture,copperOreTexture,diamondTexture,droneTexture1,gearTexture,slotTexture2,starIcon;
	public static SpaceGame INSTANCE;
	public Skin skin;
	ShapeRenderer shapeRenderer;
	public BitmapFont bitmapFont;
	public GlyphLayout textMeasurer;
	public boolean updateWorld;
	
	@Override
	public void create () {
		INSTANCE=this;
		textMeasurer=new GlyphLayout();
		bitmapFont=new BitmapFont();
		assetManager=new AssetManager();
		batch = new SpriteBatch();
		uiBatch=new SpriteBatch();
		shapeRenderer=new ShapeRenderer();
		assetManager.load("star.png",Texture.class);
		assetManager.load("green planet.png",Texture.class);
		assetManager.load("greenish planet.png",Texture.class);
		assetManager.load("mixed planet.png",Texture.class);
		assetManager.load("red ship.png",Texture.class);
		assetManager.load("scales64.png",Texture.class);
		assetManager.load("take off.png",Texture.class);
		assetManager.load("blue square.png",Texture.class);
		assetManager.load("projectile 1.png",Texture.class);
		assetManager.load("iron ore.png",Texture.class);
		assetManager.load("copper ore.png", Texture.class);
		assetManager.load("resource.png",Texture.class);
		assetManager.load("drone 1.png", Texture.class);
		assetManager.load("gears64.png", Texture.class);
		assetManager.load("slot.png",Texture.class);
		assetManager.load("star icon.png",Texture.class);

		assetManager.load("alcohol.png",Texture.class);
		assetManager.load("tools.png",Texture.class);
		assetManager.load("car parts.png",Texture.class);
		assetManager.load("firearms.png",Texture.class);
		assetManager.load("jewellery.png",Texture.class);
		assetManager.load("clothes.png",Texture.class);
		assetManager.load("electronics.png",Texture.class);
		assetManager.load("water bottle.png",Texture.class);
		assetManager.load("furniture.png",Texture.class);
		assetManager.load("food.png",Texture.class);
		assetManager.load("medicine.png",Texture.class);
		assetManager.finishLoading();

		alcoholTexture=assetManager.get("alcohol.png");
		toolsTexture=assetManager.get("tools.png");
		carPartsTexture=assetManager.get("car parts.png");
		firearmsTexture=assetManager.get("firearms.png");
		jewelleryTexture=assetManager.get("jewellery.png");
		clothesTexture=assetManager.get("clothes.png");
		electronicsTexture=assetManager.get("electronics.png");
		waterTexture=assetManager.get("water bottle.png");
		furnitureTexture=assetManager.get("furniture.png");
		foodTexture=assetManager.get("food.png");
		medicineTexture=assetManager.get("medicine.png");

		basicProjectile=assetManager.get("projectile 1.png");
		copperOreTexture=assetManager.get("copper ore.png");
		ironOreTexture=assetManager.get("iron ore.png");
		diamondTexture=assetManager.get("resource.png");
		droneTexture1=assetManager.get("drone 1.png");
		gearTexture=assetManager.get("gears64.png");
		slotTexture2=assetManager.get("slot.png");
		starIcon=assetManager.get("star icon.png");

		starTextures=new ArrayList<>(3);
		starTextures.add(assetManager.get("star.png"));
		planetTextures=new ArrayList<>();
		planetTextures.add(assetManager.get("green planet.png"));
		planetTextures.add(assetManager.get("greenish planet.png"));
		planetTextures.add(assetManager.get("mixed planet.png"));

		takeOffTexture=assetManager.get("take off.png");
		scalesTexture=assetManager.get("scales64.png");
		redStarshipTexture=assetManager.get("red ship.png");
		slotTexture=assetManager.get("blue square.png");
		skin=new Skin(Gdx.files.internal("skins/cloud/cloud-form-ui.json"));
		setScreen(new StartScreen(this));
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		starTextures.forEach(Texture::dispose);
		planetTextures.forEach(Texture::dispose);
		redStarshipTexture.dispose();
		scalesTexture.dispose();
		skin.dispose();
		uiBatch.dispose();
	}

	public void initialize()
	{
		starSystems=new Array<>();
		int x=0;
		int y=0;
		for (int i = 0; i < 10; i++) {
			StarSystem starSystem=new StarSystem(planetTextures,starTextures,x,y);
			starSystems.add(starSystem);
			x+=random.nextInt(-400,400);
			y+=random.nextInt(-400,400);
		}
		playerShip=new StarShip(0,0,0,redStarshipTexture,starSystems.get(random.nextInt(starSystems.size)));
	}

	@Override
	public void render() {
		super.render();
		if(updateWorld)
			starSystems.forEach(StarSystem::update);
	}
}
