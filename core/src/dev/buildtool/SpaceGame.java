package dev.buildtool;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;

import java.util.ArrayList;
import java.util.Random;

public class SpaceGame extends Game {
	static Random random=new Random();
	SpriteBatch worldBatch;
	SpriteBatch uiBatch;
	ArrayList<Texture> planetTextures;
	AssetManager assetManager;
	ArrayList<StarSystem> starSystems;
	ArrayList<Texture> starTextures;
	PlayerShip playerShip;
	Texture redStarshipTexture;
	public Texture scalesTexture,takeOffTexture,slotTexture,alcoholTexture,toolsTexture,carPartsTexture,firearmsTexture,
			jewelleryTexture, clothesTexture,electronicsTexture,waterTexture,furnitureTexture,foodTexture,medicineTexture,
		basicProjectile,ironOreTexture,copperOreTexture,diamondTexture,droneTexture1,gearTexture,slotTexture2,starIcon,
		targetTexture,skyTexture, tradingHull1Texture,starGateTexture,inhabitedPlanetIcon,uninhabitedPlanetIcon,
		battleHull2,pelletTexture,stargateIcon,battleHull3,pirateHull1,engine1Texture,engine2Texture,engine3Texture,shipIcon,
		ship2icon,slotTexture3,thrusters1Texture,thrusters2Texture,gunTexture,shotgunTexture,cashTexture,drone2Texture,
		containerTexture,shipIcon3,blackHullTexture,blackHull2Texture,tradingHull2Texture,pirateHull2Texture,pirateHull3Texture,
		basicGunTexture,machineGunTexture,battleHull3Texture,aiSmallHull1,aiSmallHull2,aiMediumHull1,aiMediumHull2,aiBigHull1,
			aiBigHull2,aiLargeHull1,aiLargeHull2,redProjectileTexture,clusterGunTexture;
	public static SpaceGame INSTANCE;
	public Skin skin;
	ShapeRenderer shapeRenderer,uiShapeRenderer;
	public BitmapFont bitmapFont;
	public GlyphLayout textMeasurer;
	public boolean updateWorld;
	static boolean debugDraw;
	public Sound machineGunSound,laserShotSound,blasterSound,shotGunSound;
	
	@Override
	public void create () {
		INSTANCE=this;
		textMeasurer=new GlyphLayout();
		bitmapFont=new BitmapFont();
		bitmapFont.getData().markupEnabled=true;
		assetManager=new AssetManager();
		worldBatch = new SpriteBatch();
		uiBatch=new SpriteBatch();
		shapeRenderer=new ShapeRenderer();
		uiShapeRenderer=new ShapeRenderer();
		assetManager.load("textures/star.png",Texture.class);
		assetManager.load("textures/green planet.png",Texture.class);
		assetManager.load("textures/greenish planet.png",Texture.class);
		assetManager.load("textures/mixed planet.png",Texture.class);
		assetManager.load("textures/planet3.png", Texture.class);
		assetManager.load("textures/planet5.png", Texture.class);
		assetManager.load("textures/red ship.png",Texture.class);
		assetManager.load("textures/scales64.png",Texture.class);
		assetManager.load("textures/take off.png",Texture.class);
		assetManager.load("textures/blue square.png",Texture.class);
		assetManager.load("textures/projectile 1.png",Texture.class);
		assetManager.load("textures/iron ore.png",Texture.class);
		assetManager.load("textures/copper ore.png", Texture.class);
		assetManager.load("textures/resource.png",Texture.class);
		assetManager.load("textures/drone 1.png", Texture.class);
		assetManager.load("textures/gears64.png", Texture.class);
		assetManager.load("textures/slot.png",Texture.class);
		assetManager.load("textures/star icon.png",Texture.class);
		assetManager.load("textures/target_indicator.png", Texture.class);
		assetManager.load("textures/trade hull 1.png", Texture.class);
		assetManager.load("textures/battle hull 2.png",Texture.class);
		assetManager.load("textures/pellet.png", Texture.class);
		assetManager.load("textures/cash64.png", Texture.class);

		assetManager.load("textures/alcohol.png",Texture.class);
		assetManager.load("textures/tools.png",Texture.class);
		assetManager.load("textures/car parts.png",Texture.class);
		assetManager.load("textures/firearms.png",Texture.class);
		assetManager.load("textures/jewellery.png",Texture.class);
		assetManager.load("textures/clothes.png",Texture.class);
		assetManager.load("textures/electronics.png",Texture.class);
		assetManager.load("textures/water bottle.png",Texture.class);
		assetManager.load("textures/furniture.png",Texture.class);
		assetManager.load("textures/food.png",Texture.class);
		assetManager.load("textures/medicine.png",Texture.class);
		assetManager.load("textures/sky.png", Texture.class);
		assetManager.load("textures/star gate.png", Texture.class);
		assetManager.load("textures/inhabited planet.png",Texture.class);
		assetManager.load("textures/uninhabited planet.png",Texture.class);
		assetManager.load("textures/star gate icon.png",Texture.class);
		assetManager.load("textures/battle hull 1.png", Texture.class);
		assetManager.load("textures/pirate hull 1.png", Texture.class);
		assetManager.load("textures/engine1.png", Texture.class);
		assetManager.load("textures/engine2.png", Texture.class);
		assetManager.load("textures/engine3.png", Texture.class);
		assetManager.load("textures/ship icon.png", Texture.class);
		assetManager.load("textures/ship icon2.png", Texture.class);
		assetManager.load("textures/slot 2.png",Texture.class);
		assetManager.load("textures/thrusters 1.png",Texture.class);
		assetManager.load("textures/thrusters 2.png",Texture.class);
		assetManager.load("textures/gun.png", Texture.class);
		assetManager.load("textures/shotgun.png", Texture.class);
		assetManager.load("textures/drone 1.png", Texture.class);
		assetManager.load("textures/container.png", Texture.class);
		assetManager.load("textures/ship icon2.png", Texture.class);
		assetManager.load("textures/hull2.png", Texture.class);
		assetManager.load("textures/bumblebee hull.png", Texture.class);
		assetManager.load("textures/trading hull2.png",Texture.class);
		assetManager.load("textures/pirate hull2.png", Texture.class);
		assetManager.load("textures/pirate hull3.png", Texture.class);
		assetManager.load("textures/basic gun.png", Texture.class);
		assetManager.load("textures/machine gun.png", Texture.class);
		assetManager.load("textures/battle hull 3.png", Texture.class);
		assetManager.load("textures/ai big hull 1.png",Texture.class);
		assetManager.load("textures/ai big hull 2.png",Texture.class);
		assetManager.load("textures/ai hull small 2.png",Texture.class);
		assetManager.load("textures/ai hull small 1.png",Texture.class);
		assetManager.load("textures/ai large hull 1.png",Texture.class);
		assetManager.load("textures/ai large hull 2.png",Texture.class);
		assetManager.load("textures/ai medium hull 2.png",Texture.class);
		assetManager.load("textures/ai medium hull 1.png",Texture.class);
		assetManager.load("textures/projectile2.png", Texture.class);
		loadTexture("cluster gun");
		assetManager.finishLoading();

		alcoholTexture=assetManager.get("textures/alcohol.png");
		toolsTexture=assetManager.get("textures/tools.png");
		carPartsTexture=assetManager.get("textures/car parts.png");
		firearmsTexture=assetManager.get("textures/firearms.png");
		jewelleryTexture=assetManager.get("textures/jewellery.png");
		clothesTexture=assetManager.get("textures/clothes.png");
		electronicsTexture=assetManager.get("textures/electronics.png");
		waterTexture=assetManager.get("textures/water bottle.png");
		furnitureTexture=assetManager.get("textures/furniture.png");
		foodTexture=assetManager.get("textures/food.png");
		medicineTexture=assetManager.get("textures/medicine.png");

		basicProjectile=assetManager.get("textures/projectile 1.png");
		copperOreTexture=assetManager.get("textures/copper ore.png");
		ironOreTexture=assetManager.get("textures/iron ore.png");
		diamondTexture=assetManager.get("textures/resource.png");
		droneTexture1=assetManager.get("textures/drone 1.png");
		gearTexture=assetManager.get("textures/gears64.png");
		slotTexture2=assetManager.get("textures/slot.png");
		starIcon=assetManager.get("textures/star icon.png");
		targetTexture=assetManager.get("textures/target_indicator.png");
		skyTexture=assetManager.get("textures/sky.png");
		skyTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		tradingHull1Texture=assetManager.get("textures/trade hull 1.png");
		starGateTexture=assetManager.get("textures/star gate.png");
		battleHull2=assetManager.get("textures/battle hull 2.png");
		pelletTexture=assetManager.get("textures/pellet.png");
		engine1Texture=assetManager.get("textures/engine1.png");
		engine2Texture=assetManager.get("textures/engine2.png");
		engine3Texture=assetManager.get("textures/engine3.png");
		slotTexture3=assetManager.get("textures/slot 2.png");
		thrusters1Texture=assetManager.get("textures/thrusters 1.png");
		thrusters2Texture=assetManager.get("textures/thrusters 2.png");
		gunTexture=assetManager.get("textures/gun.png");
		shotgunTexture=assetManager.get("textures/shotgun.png");
		cashTexture=assetManager.get("textures/cash64.png");
		drone2Texture=assetManager.get("textures/drone 1.png");
		containerTexture=assetManager.get("textures/container.png");
		shipIcon3=assetManager.get("textures/ship icon2.png");
		blackHullTexture=assetManager.get("textures/hull2.png");
		blackHull2Texture=assetManager.get("textures/bumblebee hull.png");
		pirateHull3Texture=assetManager.get("textures/pirate hull3.png");
		basicGunTexture=assetManager.get("textures/basic gun.png");
		machineGunTexture=assetManager.get("textures/machine gun.png");

		starTextures=new ArrayList<>(3);
		starTextures.add(assetManager.get("textures/star.png"));
		planetTextures=new ArrayList<>();
		planetTextures.add(assetManager.get("textures/green planet.png"));
		planetTextures.add(assetManager.get("textures/greenish planet.png"));
		planetTextures.add(assetManager.get("textures/mixed planet.png"));
		planetTextures.add(assetManager.get("textures/planet3.png"));
		planetTextures.add(assetManager.get("textures/planet5.png"));
		inhabitedPlanetIcon=assetManager.get("textures/inhabited planet.png");
		uninhabitedPlanetIcon=assetManager.get("textures/uninhabited planet.png");
		stargateIcon=assetManager.get("textures/star gate icon.png");
		battleHull3=assetManager.get("textures/battle hull 1.png");
		pirateHull1=assetManager.get("textures/pirate hull 1.png");
		shipIcon=assetManager.get("textures/ship icon.png");
		ship2icon=assetManager.get("textures/ship icon2.png");
		tradingHull2Texture=assetManager.get("textures/trading hull2.png");
		pirateHull2Texture=assetManager.get("textures/pirate hull2.png");
		battleHull3Texture=assetManager.get("textures/battle hull 3.png");

		takeOffTexture=assetManager.get("textures/take off.png");
		scalesTexture=assetManager.get("textures/scales64.png");
		redStarshipTexture=assetManager.get("textures/red ship.png");
		slotTexture=assetManager.get("textures/blue square.png");
		redProjectileTexture=assetManager.get("textures/projectile2.png");
		clusterGunTexture=get("cluster gun");

		aiSmallHull1=assetManager.get("textures/ai hull small 1.png");
		aiSmallHull2=assetManager.get("textures/ai hull small 2.png");
		aiMediumHull1=assetManager.get("textures/ai medium hull 1.png");
		aiMediumHull2=assetManager.get("textures/ai medium hull 2.png");
		aiBigHull1=assetManager.get("textures/ai big hull 1.png");
		aiBigHull2=assetManager.get("textures/ai big hull 2.png");
		aiLargeHull1=assetManager.get("textures/ai large hull 1.png");
		aiLargeHull2=assetManager.get("textures/ai large hull 2.png");
		skin=new Skin(Gdx.files.internal("skins/cloud/cloud-form-ui.json"));
		skin.get("font", BitmapFont.class).getData().markupEnabled=true;
		setScreen(new StartScreen(this));
		VisUI.load();

		machineGunSound=Gdx.audio.newSound(Gdx.files.internal("sounds/machine gun.wav"));
		laserShotSound=Gdx.audio.newSound(Gdx.files.internal("sounds/laser-shot.wav"));
		blasterSound=Gdx.audio.newSound(Gdx.files.internal("sounds/retro-shot-blaster.wav"));
		shotGunSound=Gdx.audio.newSound(Gdx.files.internal("sounds/shotgun-spas.mp3"));
	}

	@Override
	public void dispose () {
		worldBatch.dispose();
		starTextures.forEach(Texture::dispose);
		planetTextures.forEach(Texture::dispose);
		redStarshipTexture.dispose();
		scalesTexture.dispose();
		skin.dispose();
		uiBatch.dispose();
		VisUI.dispose();
	}

	public void initialize()
	{
		starSystems=new ArrayList<>();
		int x=0;
		int y=0;
		int planetCount=0;
		for (int i = 0; i <10; i++) {
			int xleft=random.nextInt(-300,-100);
			int xright=random.nextInt(100,300);
			int yleft=random.nextInt(-300,-100);
			int yright=random.nextInt(100,300);
			while (true) {
				boolean recalculate=false;
				for (StarSystem starSystem : starSystems) {
					if (Vector2.dst(starSystem.positionX, starSystem.positionY, x, y) < 100) {
						if (random.nextBoolean())
							x += xleft;
						else
							x += xright;
						if (random.nextBoolean())
							y += yleft;
						else
							y += yright;
						recalculate=true;
					}
				}
				if(!recalculate)
					break;
			}
			StarSystem starSystem=new StarSystem(planetTextures,starTextures,x,y, i>4);
			starSystems.add(starSystem);
			planetCount+=starSystem.planets.size();
		}
		System.out.println("Generated "+planetCount+ " planets in total");
		updateWorld=false;
	}

	@Override
	public void render() {
		super.render();
		if(updateWorld)
			starSystems.forEach(StarSystem::update);
		if(Gdx.input.isKeyJustPressed(Input.Keys.F3))
		{
			debugDraw=!debugDraw;
		}
	}

	private void loadTexture(String name)
	{
		assetManager.load("textures/"+name+".png", Texture.class);
	}

	private Texture get(String name)
	{
		return assetManager.get("textures/"+name+".png");
	}
}
